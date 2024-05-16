package com.joekomputer.android.mvifun.character.view

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import com.hoc081098.flowext.defer
import com.hoc081098.flowext.startWith
import com.joekomputer.android.mvifun.base.BaseViewModel
import com.joekomputer.android.mvifun.base.UiIntent
import com.joekomputer.android.mvifun.base.UiState
import com.joekomputer.android.mvifun.character.model.Character
import com.joekomputer.android.mvifun.character.statemappers.CharacterDetailChanges
import com.joekomputer.android.mvifun.character.usecase.GetCharactersUseCase
import com.joekomputer.android.mvifun.utils.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

@HiltViewModel
class CharacterDetailVM @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCharactersUseCase: GetCharactersUseCase
) : BaseViewModel<CharacterDetailVM.ViewState, CharacterDetailVM.ViewIntent>(ViewState.initial()) {

    private val characterName: String = checkNotNull(savedStateHandle["characterName"])

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun SharedFlow<ViewIntent>.intentToStatChangeFlow(): Flow<CharacterDetailChanges> {
        return merge(
            filterIsInstance<ViewIntent.Initial>().flatMapLatest { getCharacterDetailFromUseCase() },
        )
    }

    private fun getCharacterDetailFromUseCase(): Flow<CharacterDetailChanges> = defer(getCharactersUseCase::invoke)
        .map { result ->
            when (result.status) {
                Status.ERROR -> {
                    CharacterDetailChanges.Error(result.message ?: "Err could not load list")
                }

                Status.SUCCESS -> {
                    val character = result.data?.find {
                        it.name == characterName
                    }
                    if (character != null) {
                        CharacterDetailChanges.Data(character = character)
                    } else {
                        CharacterDetailChanges.Error(result.message ?: "could not find character in list")
                    }
                }

                Status.LOADING -> {
                    CharacterDetailChanges.Loading
                }
            }
        }
        .startWith { CharacterDetailChanges.Loading }

    @Immutable
    data class ViewState(
        val selectedCharacter: Character?,
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState {
        companion object {
            fun initial() = ViewState(
                selectedCharacter = null,
                isLoading = true,
                error = null
            )
        }
    }

    @Immutable
    sealed interface ViewIntent : UiIntent {
        object Initial : ViewIntent
    }
}
