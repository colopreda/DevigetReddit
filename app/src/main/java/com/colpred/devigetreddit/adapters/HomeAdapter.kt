package com.colpred.devigetreddit.adapters

import android.view.View
import android.view.ViewGroup
import com.colpred.devigetreddit.model.Post
import com.colpred.devigetreddit.ui.PostItemView
import com.github.stephenvinouze.advancedrecyclerview.core.adapters.RecyclerAdapter

class HomeAdapter : RecyclerAdapter<Post>() {
    override fun onBindItemView(view: View, position: Int) {
        when (view) {
            is PostItemView -> view.bind(items[position])
        }
    }

    override fun onCreateItemView(parent: ViewGroup, viewType: Int): View = PostItemView(parent.context)

}