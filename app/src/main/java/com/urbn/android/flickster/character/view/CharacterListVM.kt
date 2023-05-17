package com.urbn.android.flickster.character.view

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.hoc081098.flowext.defer
import com.hoc081098.flowext.flowFromSuspend
import com.hoc081098.flowext.startWith
import com.urbn.android.flickster.base.*
import com.urbn.android.flickster.character.model.Character
import com.urbn.android.flickster.character.usecase.GetCharactersUseCase
import com.urbn.android.flickster.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
@OptIn(FlowPreview::class)
class CharacterListVM @Inject constructor(
    private val getCharactersUseCase: GetCharactersUseCase
) : BaseViewModel<CharacterListVM.ViewState, CharacterListVM.ViewIntent>() {



    override val viewState: StateFlow<CharacterListVM.ViewState>


    init {
        val initialVS = ViewState.initial()

        viewState = intentSharedFlow
            .debugLog("ViewIntent")
            .filtered()
            .shareWhileSubscribed()
            .toStatChangeFlow()
            .debugLog("StateChange")
            .sendSingleEvent()
            .scan(initialVS) { vs, change -> change.reduce(vs) }
            .debugLog("ViewState")
            .stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                initialVS
            )
    }

    private fun Flow<CharacterListChanges>.sendSingleEvent(): Flow<CharacterListChanges> {
        return onEach { change ->
            val event = when (change) {
                is CharacterListChanges.AddFavoriteCharacter.Success -> ViewIntent.FavoriteCharacter(character = change.character)
                is CharacterListChanges.SortCharacters -> ViewIntent.SortCharacters(sortOrder = change)
                is CharacterListChanges.Data -> return@onEach
                is CharacterListChanges.Error -> return@onEach
                is CharacterListChanges.Loading -> return@onEach
            }
            sendIntent(event)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun SharedFlow<UiIntent>.toStatChangeFlow(): Flow<CharacterListChanges> {
        val stateChanges = defer(getCharactersUseCase::invoke)
            .map { result ->
                when(result.status){
                    Status.ERROR -> { CharacterListChanges.Error(result.message?:"Could not load list")}
                    Status.SUCCESS -> {
                        CharacterListChanges.Data(characterList = result.data?.toPersistentList()?: persistentListOf())
                    }
                    Status.LOADING ->{
                        CharacterListChanges.Loading
                    }
                }
            }
            .startWith { CharacterListChanges.Loading }
        return return merge(
            filterIsInstance<ViewIntent.Initial>().flatMapLatest { stateChanges },
            filterIsInstance<ViewIntent.SortCharacters>().map<ViewIntent.SortCharacters, CharacterListChanges> {
                if(it.sortOrder == CharacterListChanges.SortCharacters.Ascending){
                    CharacterListChanges.SortCharacters.Ascending
                } else{
                    CharacterListChanges.SortCharacters.Descending
                }
            }.startWith(CharacterListChanges.SortCharacters.Descending)
        )
    }

    @Immutable
    data class ViewState(
        val characters : PersistentList<Character> = persistentListOf(),
        val isLoading: Boolean = false,
        val error : String? = null
        ) : UiState{
        companion object {
            fun initial() = ViewState(
                characters = persistentListOf(),
                isLoading = true,
                error = null
            )
        }
    }

    @Immutable
    sealed interface ViewIntent : UiIntent {
        object Initial : ViewIntent

        data class FavoriteCharacter(val character: Character) : ViewIntent

        data class SortCharacters(val sortOrder: CharacterListChanges.SortCharacters) : ViewIntent

    }

    private companion object {
        private fun SharedFlow<ViewIntent>.filtered(): Flow<ViewIntent> = merge(
            filterIsInstance<ViewIntent.Initial>().take(1),
            filterNot { it is ViewIntent.Initial }
        )
    }



        sealed interface CharacterListChanges : ChangeState<ViewState> {

            override fun reduce(viewState: ViewState): ViewState {
                return when (this) {
                    Loading -> viewState.copy(
                        isLoading = true,
                        error = null
                    )
                    is Data -> viewState.copy(
                        isLoading = false,
                        error = null,
                        characters = characterList.toPersistentList()
                    )
                    is Error -> viewState.copy(
                        isLoading = false,
                        error = error
                    )
                    else -> {viewState.copy()}
                }
            }

            object Loading : CharacterListChanges
            data class Data(val characterList: List<Character>) : CharacterListChanges
            data class Error(val error: String) : CharacterListChanges

            sealed interface SortCharacters : CharacterListChanges{
                override fun reduce(viewState: ViewState): ViewState {
                   return when(this) {
                        Ascending -> viewState.copy(
                            characters = viewState.characters.sortedBy { it.name }.toPersistentList()
                        )
                        Descending -> viewState.copy(
                            characters = viewState.characters.sortedByDescending { it.name }.toPersistentList()
                        )
                    }
                }
                object Ascending : SortCharacters
                object Descending : SortCharacters
            }

            sealed interface AddFavoriteCharacter : CharacterListChanges {
                data class Success(val character: Character) : AddFavoriteCharacter

                override fun reduce(viewState: ViewState) = when (this) {
                    is Success ->  viewState.copy(
                        characters = viewState.characters.mutate { charList ->
                            charList.forEachIndexed { index, char ->
                                if (char.id == character.id) {
                                    charList[index] = char.copy(isFavorite = true)
                                    return@mutate
                                }
                            }
                        }
                    )
                }
            }
        }
    }
