package com.joekomputer.android.mvifun.character.view

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.channels.Channel

@Composable
fun CharacterDetailScreen(vm : CharacterListVM){
    val intentChannel = remember { Channel<CharacterListVM.ViewIntent>(Channel.UNLIMITED) }

    val viewState by vm.viewState.collectAsStateWithLifecycle()
    val dispatch = remember {
        { intent: CharacterListVM.ViewIntent ->
            intentChannel.trySend(intent).getOrThrow()
        }
    }

    Text(viewState.selectedCharacter?.name?:"")

}