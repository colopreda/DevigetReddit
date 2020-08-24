package com.colpred.devigetreddit.db.dao

import androidx.room.*
import com.colpred.devigetreddit.db.entity.PostStatus

@Dao
interface PostDao {

    @Query("SELECT * FROM posts")
    suspend fun getAllPostsStatus(): List<PostStatus>

    @Query("SELECT * FROM posts where id=:id")
    suspend fun getPostStatus(id: String): PostStatus

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(postStatus: List<PostStatus>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(postStatus: PostStatus)

    @Update
    suspend fun updatePost(postStatus: PostStatus)
}