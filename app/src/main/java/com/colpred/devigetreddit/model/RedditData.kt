package com.colpred.devigetreddit.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RedditData(var children: List<ChildrenData>, var after: String) : Parcelable