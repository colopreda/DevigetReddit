package com.colpred.devigetreddit.network

import com.colpred.devigetreddit.model.RedditJsonResponse

interface ApiHelper {
    suspend fun getTopPosts(): RedditJsonResponse
    suspend fun getNextTopPosts(after: String): RedditJsonResponse
}