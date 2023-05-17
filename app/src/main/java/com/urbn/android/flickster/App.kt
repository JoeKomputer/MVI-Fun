package com.urbn.android.flickster

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
import com.urbn.android.flickster.character.view.CharacterListScreen
import com.urbn.android.flickster.character.view.CharacterListVM
import com.urbn.android.flickster.navigation.Route
import com.urbn.android.flickster.theme.FlicksterTheme

@Composable
fun FlicksterApp() {
    val navController = rememberNavController()
    FlicksterTheme {
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
        CharacterListScreen(viewModel)
    }
}