package com.colpred.devigetreddit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.colpred.devigetreddit.db.DatabaseHelper
import com.colpred.devigetreddit.db.entity.PostStatus
import com.colpred.devigetreddit.model.ChildrenData
import com.colpred.devigetreddit.model.RedditJsonResponse
import com.colpred.devigetreddit.network.ApiHelper
import com.colpred.devigetreddit.utils.Resource
import com.colpred.devigetreddit.utils.notifyObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception

class HomeViewModel(private val apiHelper: ApiHelper, private val databaseHelper: DatabaseHelper) : ViewModel() {

    private var postListMutable = MutableLiveData<Resource<MutableList<ChildrenData>>>()
    private var checkedPostList = mutableListOf<ChildrenData>()
    private var lastPost: String = ""

    init {
        fetchPosts()
    }

    fun fetchPosts() {
        viewModelScope.launch {
            postListMutable.postValue(Resource.loading(null))
            try {
                var topPosts = apiHelper.getTopPosts()
                var postList = topPosts.data.children.toMutableList()
                checkedPostList = checkStatus(postList)
                lastPost = topPosts.data.after
                while (checkedPostList.size == 0) {
                    topPosts = apiHelper.getNextTopPosts(lastPost)
                    var postList = topPosts.data.children.toMutableList()
                    checkedPostList = checkStatus(postList)
                    lastPost = topPosts.data.after
                }
                postListMutable.postValue(Resource.success(checkedPostList))
            } catch (e: Exception) {
                postListMutable.postValue(Resource.error("Something Went Wrong", null))
            }
        }
    }

    private suspend fun checkStatus(topPosts: MutableList<ChildrenData>): MutableList<ChildrenData> {
        val postStatus = databaseHelper.getAllPostsStatus()
        for (status in postStatus) {
            topPosts.find { it.data.id == status.id }?.apply {
                data.clicked = status.read
            }
            topPosts.removeIf { it.data.id == status.id && status.deleted }
        }
        return topPosts
    }

    fun markAsRead(id: String) {
        postListMutable.value?.data?.find { it.data.id == id }?.apply {
            data.clicked = true
        }
        postListMutable.notifyObserver()
        viewModelScope.launch {
            try {
                databaseHelper.insertPost(PostStatus(id, read = true, deleted = false))
            } catch (e: Exception) {
                // Catch database exception
            }
        }
    }

    fun deletePost(id: String) {
        postListMutable.value?.data?.removeIf { it.data.id == id }
        viewModelScope.launch {
            try {
                databaseHelper.insertPost(PostStatus(id, read = true, deleted = true))
            } catch (e: Exception) {
                // Catch database exception
            }
        }
    }

    fun deleteAllPosts() {
        for (post in postListMutable.value?.data!!) {
            viewModelScope.launch {
                try {
                    databaseHelper.insertPost(PostStatus(post.data.id, read = true, deleted = true))
                } catch (e: Exception) {
                    // Catch database exception
                }
            }
        }
        postListMutable.value?.data?.clear()
        postListMutable.notifyObserver()
    }

    fun loadMorePosts() {
        viewModelScope.launch {
            try {
                val morePosts = apiHelper.getNextTopPosts(lastPost)
                val newPostList = morePosts.data.children.toMutableList()
                lastPost = morePosts.data.after
                val checkedTopPosts = checkStatus(newPostList)
                checkedPostList.addAll(checkedTopPosts)
                postListMutable.postValue(Resource.success(checkedPostList))
            } catch (e: Exception) {
                postListMutable.postValue(Resource.error("Something Went Wrong", null))
            }
        }
    }

    fun getPosts(): LiveData<Resource<MutableList<ChildrenData>>> {
        return postListMutable
    }

    fun hasMoreItems(): Boolean {
        return lastPost.isNotEmpty() || checkedPostList.size > 50
    }

}