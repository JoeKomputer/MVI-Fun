package com.joekomputer.android.mvifun.base

interface UiState

interface UiIntent

interface ChangeState<S : UiState> {
    fun reduce(viewState: S): S
}