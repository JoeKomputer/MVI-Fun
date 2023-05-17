package com.urbn.android.flickster.character.service

import com.urbn.android.flickster.character.model.Character
import com.urbn.android.flickster.character.model.WireApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET(".")
    suspend fun getCharacters(@Query("q", encoded = true) query : String, @Query("format") format : String = "json"): Response<WireApiResponse>
}
