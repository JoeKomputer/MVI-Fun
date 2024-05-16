package com.joekomputer.android.mvifun.character.service

import com.joekomputer.android.mvifun.character.model.Character
import com.joekomputer.android.mvifun.utils.NetworkResult
import com.joekomputer.android.mvifun.utils.Status
import com.joekomputer.android.mvifun.utils.safeApiCall
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.withContext
import timber.log.Timber

const val IMG_BASE_URL = "https://www.duckduckgo.com"

interface CharacterRepository {
    fun getCharacters(query: String): Flow<NetworkResult<List<Character>>>
}

@ActivityRetainedScoped
class CharacterRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : CharacterRepository {

    private sealed class Change {
        class Refreshed(val characters: NetworkResult<List<Character>>) : Change()
    }

    override fun getCharacters(query: String): Flow<NetworkResult<List<Character>>> = flow {
        val initial = getCharactersFromRemote(query)

        changesFlow
            .onEach { Timber.d("[CHAR_REPO] Change=$it") }
            .scan(initial) { acc, change ->
                when (change) {
                    is Change.Refreshed -> change.characters
                }
            }
            .onEach { Timber.d("[CHAR_REPO] Emit chars.size=${it.data?.size} ") }
            .let { emitAll(it) }
    }

    private val changesFlow = MutableSharedFlow<Change>(extraBufferCapacity = 64)

    private suspend inline fun sendChange(change: Change) = changesFlow.emit(change)

    private suspend fun getCharactersFromRemote(query: String): NetworkResult<List<Character>> {
        return withContext(Dispatchers.IO) {
            safeApiCall { apiService.getCharacters(query) }
        }.let {
            when (it.status) {
                Status.SUCCESS -> NetworkResult.success(it.data?.RelatedTopics?.map {
                    Character(
                        name = it.Text.substringBefore("-"),
                        details = it.Text.substringAfter("-"),
                        imageUrl = IMG_BASE_URL + it.Icon.URL
                    )
                })

                Status.ERROR -> NetworkResult.error(
                    it.message ?: "ERROR"
                )

                Status.LOADING -> NetworkResult.loading(null)
            }
        }
    }
}