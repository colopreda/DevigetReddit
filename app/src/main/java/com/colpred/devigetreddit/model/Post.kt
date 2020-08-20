package com.colpred.devigetreddit.model

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "posts")
data class Post(
    @NonNull
    @PrimaryKey(autoGenerate = false) var id: String,
    @SerializedName("author_fullname") var author: String?,
    @SerializedName("subreddit_name_prefixed") var subreddit: String?,
    var title: String?,
    var clicked: Boolean = false,
    var thumbnail: String?,
    var url: String?,
    @SerializedName("created_utc") var created: Long,
    @SerializedName("num_comments") var numComments: Int?
) : Parcelable