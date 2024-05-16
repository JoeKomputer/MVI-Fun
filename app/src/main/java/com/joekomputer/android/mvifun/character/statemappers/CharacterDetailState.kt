package com.joekomputer.android.mvifun.character.statemappers

import com.joekomputer.android.mvifun.base.ChangeState
import com.joekomputer.android.mvifun.character.model.Character
import com.joekomputer.android.mvifun.character.view.CharacterDetailVM

sealed interface CharacterDetailChanges : ChangeState<CharacterDetailVM.ViewState> {
    override fun reduce(viewState: CharacterDetailVM.ViewState): CharacterDetailVM.ViewState {
        return when (this) {
            Loading -> viewState.copy(
                isLoading = true,
                error = null
            )

            is Data -> viewState.copy(
                selectedCharacter = character,
                isLoading = false,
                error = null,
            )

            is Error -> viewState.copy(
                isLoading = false,
                error = error
            )
        }
    }

    object Loading : CharacterDetailChanges
    data class Data(val character: Character?) : CharacterDetailChanges
    data class Error(val error: String) : CharacterDetailChanges
}