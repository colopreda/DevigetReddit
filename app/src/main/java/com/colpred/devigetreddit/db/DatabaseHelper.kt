package com.colpred.devigetreddit.db

import com.colpred.devigetreddit.db.entity.PostStatus

interface DatabaseHelper {
    suspend fun getAllPostsStatus(): List<PostStatus>

    suspend fun getPostWithId(id: String): PostStatus

    suspend fun insertPosts(posts: List<PostStatus>)

    suspend fun insertPost(post: PostStatus)

    suspend fun updatePostStatus(post: PostStatus)
}