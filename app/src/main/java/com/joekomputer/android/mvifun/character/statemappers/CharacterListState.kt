package com.joekomputer.android.mvifun.character.statemappers

import com.joekomputer.android.mvifun.base.ChangeState
import com.joekomputer.android.mvifun.character.model.Character
import com.joekomputer.android.mvifun.character.view.CharacterListVM
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.toPersistentList

sealed interface CharacterListChanges : ChangeState<CharacterListVM.ViewState> {
    override fun reduce(viewState: CharacterListVM.ViewState): CharacterListVM.ViewState {
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

            is FavoriteCharacter -> viewState.copy(
                characters = viewState.characters.mutate { charList ->
                    charList.forEachIndexed { index, char ->
                        if (char.id == character.id) {
                            charList[index] = char.copy(isFavorite = !char.isFavorite)
                            return@mutate
                        }
                    }
                }
            )

            is SelectedCharacter -> viewState.copy(selectedCharacter = character)
            is SortCharacters -> when (this.sortOrder) {
                CharacterListVM.ViewState.SortOrder.Ascending -> viewState.copy(
                    characters = viewState.characters.sortedBy { it.name }.toPersistentList()
                )

                CharacterListVM.ViewState.SortOrder.Descending -> viewState.copy(
                    characters = viewState.characters.sortedByDescending { it.name }.toPersistentList()
                )
            }
        }
    }

    object Loading : CharacterListChanges
    data class Data(val characterList: List<Character>) : CharacterListChanges
    data class Error(val error: String) : CharacterListChanges
    data class FavoriteCharacter(val character: Character) : CharacterListChanges
    data class SelectedCharacter(val character: Character) : CharacterListChanges
    data class SortCharacters(val sortOrder: CharacterListVM.ViewState.SortOrder) : CharacterListChanges
}