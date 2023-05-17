package com.urbn.android.flickster.base

import androidx.annotation.CheckResult
import kotlinx.coroutines.flow.Flow

interface BaseMviView<
        I : UiIntent,
        S : UiState> {
    /**
     * Entry point for the [BaseMviView] to render itself based on a [UiState].
     */
    fun render(viewState: S)

    /**
     * Unique [Flow] used by the [BaseViewModel] to listen to the [BaseMviView].
     * All the [BaseMviView]'s [UiIntent]s must go through this [Flow].
     */
    @CheckResult
    fun viewIntents(): Flow<I>
}

interface UiState

interface UiIntent

interface ChangeState<S : UiState> {
    fun reduce(viewState: S):S
}