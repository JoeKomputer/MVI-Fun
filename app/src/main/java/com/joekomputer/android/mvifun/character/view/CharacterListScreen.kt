package com.joekomputer.android.mvifun.character.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.hoc081098.flowext.startWith
import com.joekomputer.android.mvifun.character.model.Character
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

@Composable
fun CharacterListScreen(vm : CharacterListVM){
    val intentChannel = remember { Channel<CharacterListVM.ViewIntent>(Channel.UNLIMITED) }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.Main.immediate) {
            intentChannel
                .consumeAsFlow()
                .startWith(CharacterListVM.ViewIntent.Initial)
                .onEach(vm::processIntent).collect()
        }
    }

    val viewState by vm.viewState.collectAsStateWithLifecycle()
    val dispatch = remember {
        { intent: CharacterListVM.ViewIntent ->
            intentChannel.trySend(intent).getOrThrow()
        }
    }

    CharacterListContent(
        viewState = viewState,
        sortCharacters = { dispatch(CharacterListVM.ViewIntent.SortCharacters(it)) },
        favoriteCharacter = { dispatch(CharacterListVM.ViewIntent.FavoriteCharacter(it)) },
        characterSelected = { dispatch(CharacterListVM.ViewIntent.CharacterSelected(it)) }
    )

}

@Composable
fun CharacterListContent(
    viewState: CharacterListVM.ViewState,
    favoriteCharacter: (Character) -> Unit,
    sortCharacters: (CharacterListVM.ViewState.SortOrder) -> Unit,
    characterSelected: (Character) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        modifier = modifier.fillMaxSize(),
        visible = viewState.isLoading,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        CircularProgressIndicator()
    }

    CharacterList(
        characters = viewState.characters,
        sortOrder = viewState.sortedOrder,
        favoriteCharacter = favoriteCharacter,
        sortCharacters = sortCharacters,
        characterSelected = characterSelected
    )
}

@Composable
fun CharacterList(
    characters: ImmutableList<Character>,
    sortOrder : CharacterListVM.ViewState.SortOrder,
    sortCharacters: (CharacterListVM.ViewState.SortOrder) -> Unit,
    favoriteCharacter: (Character) -> Unit,
    characterSelected: (Character) -> Unit,
    modifier: Modifier = Modifier
) {
    var toggleSort by rememberSaveable{ mutableStateOf(true) }
    LazyColumn {
        item {
            Row(
                modifier = modifier
                    .clickable {
                        toggleSort = !toggleSort
                        sortCharacters.invoke(
                            if (toggleSort) {
                                CharacterListVM.ViewState.SortOrder.Ascending
                            } else {
                                CharacterListVM.ViewState.SortOrder.Descending
                            }
                        )
                    }
                    .background(MaterialTheme.colors.surface)
                    .padding(all = 8.dp),
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .background(MaterialTheme.colors.surface)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if(toggleSort){
                                Icons.Default.KeyboardArrowDown
                            }else{
                                Icons.Default.KeyboardArrowUp
                            },
                            contentDescription = "",
                            modifier = Modifier.size(16.dp),
                            tint = Color.Black
                        )
                        Text(
                            modifier = Modifier,
                            text = "Sort by name",
                            style = MaterialTheme.typography.h6,
                            maxLines = 1,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            Divider(
                modifier = Modifier.padding(horizontal = 8.dp),
            )
        }
        itemsIndexed(
            items = characters,
            key = { _, item -> item.id },
        ) { index, char ->
            CharacterItemRow(
                item = char,
                characterSelected = characterSelected,
                onFavorite = { favoriteCharacter(char) },
            )

            if (index < characters.lastIndex) {
                Divider(
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun CharacterItemRow(
    item: Character,
    onFavorite: () -> Unit,
    modifier: Modifier = Modifier,
    imageSize: Dp = 72.dp,
    characterSelected: (Character) -> Unit,
    padding: Dp = 8.dp,
    dismissBackgroundColor: Color = MaterialTheme.colors.secondaryVariant,
) {
    val favoriteState = rememberDismissState(
        confirmStateChange = { dismissValue ->
            if (dismissValue == DismissValue.DismissedToStart) {
                onFavorite()
            }
            false
        }
    )

    SwipeToDismiss(state = favoriteState,
        background = {
            val scale by animateFloatAsState(
                if (favoriteState.targetValue == DismissValue.Default) {
                    0.75f
                } else {
                    1f
                }
            )

            Box(
                Modifier
                    .fillMaxSize()
                    .background(dismissBackgroundColor)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "",
                    modifier = Modifier.scale(scale),
                    tint = Color.White
                )
            }
        },
        directions = setOf(DismissDirection.EndToStart),
        dismissThresholds = { FractionalThreshold(0.25f) },
        ) {
            CharacterRowContent(
                item = item,
                characterSelected = characterSelected,
                imageSize = imageSize,
                padding = padding
            )
        }
}

@Composable
fun CharacterRowContent(
    item: Character,
    modifier: Modifier = Modifier,
    characterSelected: (Character) -> Unit,
    imageSize: Dp = 72.dp,
    padding: Dp = 8.dp){
    Row(
        modifier = modifier
            .background(MaterialTheme.colors.background)
            .clickable { characterSelected.invoke(item) }
            .padding(all = padding),
    ) {
        CharacterImage(
            item = item,
            imageSize = imageSize,
        )

        Spacer(modifier = Modifier.width(padding))

        Column(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = item.name,
                style = MaterialTheme.typography.h6,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(padding))

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = item.details,
                style = MaterialTheme.typography.body2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun CharacterImage(
    item: Character,
    imageSize: Dp,
    modifier: Modifier = Modifier,
) {

    Box(modifier = Modifier
        .size(imageSize)) {
        SubcomposeAsyncImage(
            model = item.imageUrl,
            loading = {
                CircularProgressIndicator()
            },
            contentDescription = "character image of + ${item.name}"
        )
        if(item.isFavorite){
            Box(modifier = Modifier.align(TopStart)){
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "",
                    modifier = Modifier.size(22.dp),
                    tint = Color.Blue
                )
            }
        }
    }
}
