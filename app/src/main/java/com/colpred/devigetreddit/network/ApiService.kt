package com.colpred.devigetreddit.network

import com.colpred.devigetreddit.model.RedditJsonResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("r/all/top.json?limit=5")
    suspend fun getAllTopPosts(): RedditJsonResponse

    @GET("r/all/top.json?limit=5")
    suspend fun getNextTopPosts(@Query("after") after: String): RedditJsonResponse
}