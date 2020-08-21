package com.colpred.devigetreddit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.colpred.devigetreddit.model.RedditJsonResponse
import com.colpred.devigetreddit.network.ApiHelper
import com.colpred.devigetreddit.utils.Resource
import kotlinx.coroutines.launch
import java.lang.Exception

class HomeViewModel(private val apiHelper: ApiHelper) : ViewModel() {

    private var posts = MutableLiveData<Resource<RedditJsonResponse>>()
    private var lastPost: String = ""

    init {
        fetchPosts()
    }

    fun fetchPosts() {
        viewModelScope.launch {
            posts.postValue(Resource.loading(null))
            try {
                val topPosts = apiHelper.getTopPosts()
                lastPost = topPosts.data.after
                posts.postValue(Resource.success(topPosts))
            } catch (e: Exception) {
                posts.postValue(Resource.error("Something Went Wrong", null))
            }
        }
    }

    fun loadMorePosts() {
        viewModelScope.launch {
            posts.postValue(Resource.loading(null))
            try {
                val morePosts = apiHelper.getNextTopPosts(lastPost)
                lastPost = morePosts.data.after
                posts.postValue(Resource.success(morePosts))
            } catch (e: Exception) {
                posts.postValue(Resource.error("Something Went Wrong", null))
            }
        }
    }

    fun getPosts(): LiveData<Resource<RedditJsonResponse>> {
        return posts
    }

    fun hasMoreItems(): Boolean {
        return lastPost.isNotEmpty()
    }

}