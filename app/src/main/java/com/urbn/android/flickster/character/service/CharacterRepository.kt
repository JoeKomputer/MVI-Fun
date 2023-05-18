package com.urbn.android.flickster.character.service

import com.urbn.android.flickster.character.model.Character
import com.urbn.android.flickster.utils.NetworkResult
import com.urbn.android.flickster.utils.Status
import com.urbn.android.flickster.utils.safeApiCall
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject


const val IMG_BASE_URL = "https://www.duckduckgo.com"
interface CharacterRepository {
    fun getCharacters(query : String): Flow<NetworkResult<List<Character>>>
}
@ActivityRetainedScoped
class CharacterRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : CharacterRepository{


    private sealed class Change {
        class Refreshed(val characters: NetworkResult<List<Character>>) : Change()
    }

    override fun getCharacters(query : String): Flow<NetworkResult<List<Character>>> = flow {
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

    private suspend fun getCharactersFromRemote(query : String): NetworkResult<List<Character>> {
        return withContext(Dispatchers.IO) {
            safeApiCall { apiService.getCharacters(query) }
        }.let {
            when(it.status){
              Status.SUCCESS ->  NetworkResult.success(it.data?.RelatedTopics?.map {
                    Character(
                        name = it.Text.substringBefore("-"),
                        details = it.Text.substringAfter("-"),
                        imageUrl = IMG_BASE_URL + it.Icon.URL
                    )
                })
                Status.ERROR -> NetworkResult.error(
                    it.message?:"ERROR"
                )
                Status.LOADING -> NetworkResult.loading(null)
            }
        }
    }

}