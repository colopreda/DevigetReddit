package com.colpred.devigetreddit.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.colpred.devigetreddit.R
import com.colpred.devigetreddit.model.Post
import com.colpred.devigetreddit.ui.PostItemView
import com.github.stephenvinouze.advancedrecyclerview.pagination.adapters.RecyclerPaginationAdapter

class HomeAdapter(private val listener: PostItemListener) : RecyclerPaginationAdapter<Post>() {

    interface PostItemListener {
        fun onClickedPost(post: Post)
    }

    override fun onBindItemView(view: View, position: Int) {
        when (view) {
            is PostItemView -> view.bind(items[position]){
                listener.onClickedPost(it)
            }
        }
    }

    override fun onCreateItemView(parent: ViewGroup, viewType: Int): View = PostItemView(parent.context)

    override fun onCreateLoaderView(parent: ViewGroup, viewType: Int): View =
        LayoutInflater.from(parent.context).inflate(R.layout.view_progress, parent, false)

}