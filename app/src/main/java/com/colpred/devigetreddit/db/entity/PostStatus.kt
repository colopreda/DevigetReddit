package com.colpred.devigetreddit.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostStatus(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "read") val read: Boolean = false,
    @ColumnInfo(name = "deleted") val deleted: Boolean = false
)