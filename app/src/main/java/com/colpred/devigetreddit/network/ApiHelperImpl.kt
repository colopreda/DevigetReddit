package com.colpred.devigetreddit.network

import com.colpred.devigetreddit.model.RedditJsonResponse

class ApiHelperImpl(private val apiService: ApiService) : ApiHelper {
    override suspend fun getTopPosts(): RedditJsonResponse = apiService.getAllTopPosts()
    override suspend fun getNextTopPosts(after: String): RedditJsonResponse = apiService.getNextTopPosts(after)
}