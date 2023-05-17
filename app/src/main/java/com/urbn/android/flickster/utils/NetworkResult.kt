package com.urbn.android.flickster.utils

import retrofit2.Response

data class NetworkResult<out T>(val status: Status, val data: T?, val message: String?) {

    companion object {

        fun <T> success(data: T?): NetworkResult<T> {
            return NetworkResult(Status.SUCCESS, data, null)
        }

        fun <T> error(msg: String): NetworkResult<T> {
            return NetworkResult(Status.ERROR,null, msg)
        }

        fun <T> loading(data: T?): NetworkResult<T> {
            return NetworkResult(Status.LOADING, data, null)
        }

    }
}


    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                body?.let {
                    return NetworkResult.success(body)
                }
            }
            return error("${response.code()} ${response.message()}")
        } catch (e: Exception) {
            return error(e.message ?: e.toString())
        }
    }
    private fun <T> error(errorMessage: String): NetworkResult<T> =
        NetworkResult.error("Api call failed $errorMessage")


enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}
