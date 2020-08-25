package com.colpred.devigetreddit.utils

import android.text.format.DateUtils
import androidx.lifecycle.MutableLiveData

fun <T> MutableLiveData<T>.notifyObserver() {
    this.value = this.value
}

fun getTimeAgo(dateUTC: Long): String {
    return DateUtils.getRelativeTimeSpanString(dateUTC * 1000L).toString()
}