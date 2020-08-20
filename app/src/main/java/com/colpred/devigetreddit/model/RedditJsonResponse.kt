package com.colpred.devigetreddit.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RedditJsonResponse(var data: RedditData) : Parcelable