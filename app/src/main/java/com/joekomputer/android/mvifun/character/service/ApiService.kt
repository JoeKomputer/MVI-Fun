package com.joekomputer.android.mvifun.character.service

import com.joekomputer.android.mvifun.character.model.WireApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET(".")
    suspend fun getCharacters(
        @Query("q", encoded = true) query: String,
        @Query("format") format: String = "json"
    ): Response<WireApiResponse>
}
