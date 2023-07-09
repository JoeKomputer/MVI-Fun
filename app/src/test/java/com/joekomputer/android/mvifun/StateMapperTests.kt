package com.joekomputer.android.mvifun

import com.joekomputer.android.mvifun.character.model.Character
import com.joekomputer.android.mvifun.character.statemappers.CharacterListChanges
import com.joekomputer.android.mvifun.character.view.CharacterListVM
import kotlinx.collections.immutable.persistentListOf
import org.junit.Test

import org.junit.Assert.*

class StateMapperTests {

    val vs = CharacterListVM.ViewState(
        characters = persistentListOf(
            Character(
                name = "Test1",
                details = "TEST DET1",
                imageUrl = ""
            ),
            Character(
                name = "Test2",
                details = "TEST DET1",
                imageUrl = ""
            ),
            Character(
                name = "Test3",
                details = "TEST DET1",
                imageUrl = ""
            )
        )
    )

    @Test
    fun favoriteCharacterUpdatesState() {
        CharacterListChanges.FavoriteCharacter(
            character = Character(
                name = "Test1",
                details = "TEST DET1",
                imageUrl = ""
            )
        ).reduce(
            vs
        ).run {
            assertEquals(
                this,
                CharacterListVM.ViewState(
                    characters = persistentListOf(
                        Character(
                            name = "Test1",
                            details = "TEST DET1",
                            imageUrl = "",
                            isFavorite = true
                        ),
                        Character(
                            name = "Test2",
                            details = "TEST DET1",
                            imageUrl = ""
                        ),
                        Character(
                            name = "Test3",
                            details = "TEST DET1",
                            imageUrl = ""
                        )
                    )
                )
            )
        }
    }

    @Test
    fun removeFavorite(){
        val vs = CharacterListVM.ViewState(
            characters = persistentListOf(
                Character(
                    name = "Test1",
                    details = "TEST DET1",
                    imageUrl = "",
                    isFavorite = true
                ),
                Character(
                    name = "Test2",
                    details = "TEST DET1",
                    imageUrl = ""
                ),
                Character(
                    name = "Test3",
                    details = "TEST DET1",
                    imageUrl = ""
                )
            )
        )

        CharacterListChanges.FavoriteCharacter(
            character = Character(
                name = "Test1",
                details = "TEST DET1",
                imageUrl = "",
                isFavorite = true
            )
        ).reduce(
            vs
        ).run {
            assertEquals(
                this,
                CharacterListVM.ViewState(
                    characters = persistentListOf(
                        Character(
                            name = "Test1",
                            details = "TEST DET1",
                            imageUrl = "",
                            isFavorite = false
                        ),
                        Character(
                            name = "Test2",
                            details = "TEST DET1",
                            imageUrl = ""
                        ),
                        Character(
                            name = "Test3",
                            details = "TEST DET1",
                            imageUrl = ""
                        )
                    )
                )
            )
        }
    }
}