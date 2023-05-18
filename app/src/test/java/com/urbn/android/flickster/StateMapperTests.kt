package com.urbn.android.flickster

import com.urbn.android.flickster.character.model.Character
import com.urbn.android.flickster.character.statemappers.CharacterListChanges
import com.urbn.android.flickster.character.view.CharacterListVM
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
}