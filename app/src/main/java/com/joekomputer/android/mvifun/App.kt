package com.joekomputer.android.mvifun

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.joekomputer.android.mvifun.character.view.CharacterDetailScreen
import com.joekomputer.android.mvifun.character.view.CharacterDetailVM
import com.joekomputer.android.mvifun.character.view.CharacterListScreen
import com.joekomputer.android.mvifun.character.view.CharacterListVM
import com.joekomputer.android.mvifun.navigation.Route
import com.joekomputer.android.mvifun.theme.BaseTheme

@Composable
fun BaseAppComposable() {
    val navController = rememberNavController()
    BaseTheme {
        Scaffold(
            content = { padding ->
                NavHost(
                    navController = navController,
                    startDestination = Route.CharacterListScreen,
                    modifier = Modifier.padding(padding)
                ) {
                    mainScreenRoute(navController = navController)
                }
            }
        )
    }
}

private fun NavGraphBuilder.mainScreenRoute(navController: NavController) {
    composable(Route.CharacterListScreen) {
        val viewModel = hiltViewModel<CharacterListVM>()
        CharacterListScreen(viewModel, navController)
    }
    composable("${Route.CharacterDetailsScreen}/{characterName}") {
        val viewModel = hiltViewModel<CharacterDetailVM>()
        CharacterDetailScreen(viewModel)
    }
}