package com.joekomputer.android.mvifun.character.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.hoc081098.flowext.startWith
import com.joekomputer.android.mvifun.character.model.Character
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

@Composable
fun CharacterDetailScreen(vm: CharacterDetailVM) {
    LaunchedEffect(Unit) {
        withContext(Dispatchers.Main) {
            vm.eventChannel
                .startWith(CharacterDetailVM.ViewIntent.Initial)
                .onEach(vm::processIntent).collect()
        }
    }

    val viewState by vm.viewState.collectAsStateWithLifecycle()
    CharacterDetailContent(viewState = viewState)
}

@Composable
fun CharacterDetailContent(
    modifier: Modifier = Modifier,
    viewState: CharacterDetailVM.ViewState
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (viewState.selectedCharacter == null) {
            AnimatedVisibility(
                modifier = modifier.fillMaxSize(),
                visible = viewState.isLoading,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    CharacterDetailImage(
                        imageSize = 300.dp,
                        character = viewState.selectedCharacter
                    )
                }
                Row {
                    Text(
                        text = viewState.selectedCharacter.name,
                        style = TextStyle(
                            fontSize = 18.sp
                        )
                    )
                }
                Row(modifier = Modifier.padding(top = 12.dp)) {
                    Text(
                        text = viewState.selectedCharacter.details,
                        style = TextStyle(
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun CharacterDetailImage(
    modifier: Modifier = Modifier,
    character: Character,
    imageSize: Dp
) {
    SubcomposeAsyncImage(
        modifier = modifier.size(imageSize),
        model = character.imageUrl,
        contentScale = ContentScale.FillWidth,
        loading = {
            CircularProgressIndicator()
        },
        contentDescription = "character image of + ${character.name}"
    )
}