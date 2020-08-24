package com.colpred.devigetreddit.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.colpred.devigetreddit.db.dao.PostDao
import com.colpred.devigetreddit.db.entity.PostStatus

@Database(entities = [PostStatus::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postsDao(): PostDao
}