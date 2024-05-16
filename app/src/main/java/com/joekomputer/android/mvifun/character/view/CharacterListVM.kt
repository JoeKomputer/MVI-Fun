package com.joekomputer.android.mvifun.character.view

import androidx.compose.runtime.Immutable
import com.hoc081098.flowext.defer
import com.hoc081098.flowext.startWith
import com.joekomputer.android.mvifun.base.BaseViewModel
import com.joekomputer.android.mvifun.base.UiIntent
import com.joekomputer.android.mvifun.base.UiState
import com.joekomputer.android.mvifun.character.model.Character
import com.joekomputer.android.mvifun.character.statemappers.CharacterListChanges
import com.joekomputer.android.mvifun.character.usecase.GetCharactersUseCase
import com.joekomputer.android.mvifun.utils.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

@HiltViewModel
class CharacterListVM @Inject constructor(
    private val getCharactersUseCase: GetCharactersUseCase
) : BaseViewModel<CharacterListVM.ViewState, CharacterListVM.ViewIntent>(ViewState.initial()) {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun SharedFlow<ViewIntent>.intentToStatChangeFlow(): Flow<CharacterListChanges> {
        return merge(
            filterIsInstance<ViewIntent.Initial>().flatMapLatest { getCharacterListFromUseCase() },
            filterIsInstance<ViewIntent.SortCharacters>().map { CharacterListChanges.SortCharacters(it.sortOrder) },
            filterIsInstance<ViewIntent.FavoriteCharacter>().map { CharacterListChanges.FavoriteCharacter(it.character) },
            filterIsInstance<ViewIntent.CharacterSelected>().map { CharacterListChanges.SelectedCharacter(it.character) }
        )
    }

    private fun getCharacterListFromUseCase(): Flow<CharacterListChanges> = defer(getCharactersUseCase::invoke)
        .map { result ->
            when (result.status) {
                Status.ERROR -> {
                    CharacterListChanges.Error(result.message ?: "Err could not load list")
                }

                Status.SUCCESS -> {
                    CharacterListChanges.Data(characterList = result.data?.toPersistentList() ?: persistentListOf())
                }

                Status.LOADING -> {
                    CharacterListChanges.Loading
                }
            }
        }
        .startWith { CharacterListChanges.Loading }

    @Immutable
    data class ViewState(
        val characters: PersistentList<Character> = persistentListOf(),
        val selectedCharacter: Character? = null,
        val sortedOrder: SortOrder = SortOrder.Descending,
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState {
        enum class SortOrder {
            Ascending,
            Descending
        }

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

        data class SortCharacters(val sortOrder: ViewState.SortOrder) : ViewIntent

        data class CharacterSelected(val character: Character) : ViewIntent
    }
}
