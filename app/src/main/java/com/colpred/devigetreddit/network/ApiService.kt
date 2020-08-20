package com.colpred.devigetreddit.network

import com.colpred.devigetreddit.model.RedditJsonResponse
import retrofit2.http.GET

interface ApiService {
    @GET("r/all/top.json")
    suspend fun getAllTopPosts(): RedditJsonResponse
}