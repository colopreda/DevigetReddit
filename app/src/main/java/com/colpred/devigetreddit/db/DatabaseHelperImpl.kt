package com.colpred.devigetreddit.db

import com.colpred.devigetreddit.db.entity.PostStatus

class DatabaseHelperImpl(private val appDatabase: AppDatabase) : DatabaseHelper {
    override suspend fun getAllPostsStatus(): List<PostStatus> = appDatabase.postsDao().getAllPostsStatus()

    override suspend fun getPostWithId(id: String): PostStatus = appDatabase.postsDao().getPostStatus(id)

    override suspend fun insertPosts(posts: List<PostStatus>) = appDatabase.postsDao().insertAll(posts)

    override suspend fun insertPost(post: PostStatus) = appDatabase.postsDao().insertPost(post)

    override suspend fun updatePostStatus(post: PostStatus) = appDatabase.postsDao().updatePost(post)
}