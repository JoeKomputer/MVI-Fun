package com.joekomputer.android.mvifun.base

import androidx.annotation.MainThread
import kotlinx.coroutines.flow.StateFlow

interface BaseStateIntent<S : UiState, I : UiIntent> {

    val viewState: StateFlow<S>

    @MainThread
    suspend fun processIntent(intent: I)

}