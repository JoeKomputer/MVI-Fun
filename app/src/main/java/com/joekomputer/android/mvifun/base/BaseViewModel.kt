package com.joekomputer.android.mvifun.base

import android.os.Build
import androidx.annotation.CallSuper
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joekomputer.android.mvifun.BuildConfig
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.onSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber

abstract class BaseViewModel<S : UiState, I : UiIntent>(initialVs: S) : BaseStateIntent<S, I>, ViewModel() {
    protected open val rawLogTag: String? = null

    private val intentMutableFlow = MutableSharedFlow<I>(extraBufferCapacity = Int.MAX_VALUE)
    protected val intentSharedFlow: SharedFlow<I> get() = intentMutableFlow
    final override val viewState: StateFlow<S>

    @MainThread
    final override suspend fun processIntent(intent: I) {
        check(intentMutableFlow.tryEmit(intent)) { "Failed to emit intent: $intent" }
        Timber.tag(logTag).d("processIntent: $intent")
    }

    private val _eventChannel = Channel<I>(Channel.UNLIMITED)

    val eventChannel: Flow<I> by lazy { _eventChannel.receiveAsFlow() }

    val singleIntent: Flow<I> = _eventChannel.receiveAsFlow()

    init {
        viewState = intentSharedFlow
            .shareWhileSubscribed()
            .callIntentToStateChangeFlow()
            .debugLog("StateChange")
            .scan(initialVs) { vs, change ->
                change.reduce(vs)
            }
            .stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                initialVs
            )
    }

    fun sendIntent(intent: I) {
        _eventChannel.trySend(intent)
            .onSuccess { Timber.tag(logTag).d("sendIntent: event=$intent") }
            .onFailure {
                Timber
                    .tag(logTag)
                    .e(it, "Failed to send intent: $intent")
            }
            .getOrThrow()
    }

    /**
     * Take an intent and convert it to a StateChange
     * ex: user clicks checkbox (intent) -> submit button becomes valid(changed state)
     */
    abstract fun SharedFlow<I>.intentToStatChangeFlow(): Flow<ChangeState<S>>

    private final fun SharedFlow<I>.callIntentToStateChangeFlow(): Flow<ChangeState<S>> = intentToStatChangeFlow()
    protected fun <T> SharedFlow<T>.debugLog(subject: String): SharedFlow<T> =
        if (BuildConfig.DEBUG) {
            val self = this

            object : SharedFlow<T> by self {
                val subscriberCount = AtomicInteger(0)

                override suspend fun collect(collector: FlowCollector<T>): Nothing {
                    val count = subscriberCount.getAndIncrement()

                    self.collect {
                        Timber.tag(logTag).d(">>> $subject ~ $count: $it")
                        collector.emit(it)
                    }
                }
            }
        } else {
            this
        }

    protected fun <T> Flow<T>.shareWhileSubscribed(): SharedFlow<T> =
        shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    protected fun <T> Flow<T>.debugLog(subject: String): Flow<T> =
        if (BuildConfig.DEBUG) {
            onEach { Timber.tag(logTag).d(">>> $subject: $it") }
        } else {
            this
        }

    @CallSuper
    override fun onCleared() {
        super.onCleared()
        Timber.tag(logTag).d("onCleared")
    }

    protected val logTag by lazy(LazyThreadSafetyMode.PUBLICATION) {
        (rawLogTag ?: this::class.java.simpleName).let { tag: String ->
            // Tag length limit was removed in API 26.
            if (tag.length <= MAX_TAG_LENGTH || Build.VERSION.SDK_INT >= 26) {
                tag
            } else {
                tag.take(MAX_TAG_LENGTH)
            }
        }
    }

    private companion object {
        private const val MAX_TAG_LENGTH = 23
    }
}

