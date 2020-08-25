package com.colpred.devigetreddit.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.colpred.devigetreddit.R
import com.colpred.devigetreddit.adapters.HomeAdapter
import com.colpred.devigetreddit.model.Post
import com.colpred.devigetreddit.utils.getTimeAgo
import kotlinx.android.synthetic.main.item_post.view.*

class PostItemView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attributeSet, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.item_post, this, true)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    fun bind (post: Post, onClickListener: HomeAdapter.PostItemListener, position: Int) {
        setOnClickListener {
            onClickListener.onClickedPost(post)
        }
        title.text = post.title
        subreddit.text = post.subreddit
        comments.text = post.numComments.toString()
        if (post.clicked) {
            indicator.visibility = View.GONE
        } else {
            indicator.visibility = View.VISIBLE
        }
        delete_ico.setOnClickListener {
            onClickListener.onDeleteClicked(post, position)
        }
        posted_by.text = "Posted by ${post.author} - ${getTimeAgo(post.created)}"
        if (post.thumbnail == "default") {
            thumbnail.visibility = View.GONE
        } else {
            Glide.with(context)
                .load(post.thumbnail)
                .centerCrop()
                .into(thumbnail)
        }
    }
}