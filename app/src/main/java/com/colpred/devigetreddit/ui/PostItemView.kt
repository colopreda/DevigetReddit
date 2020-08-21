package com.colpred.devigetreddit.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.colpred.devigetreddit.R
import com.colpred.devigetreddit.model.Post
import kotlinx.android.synthetic.main.item_post.view.*

class PostItemView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attributeSet, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.item_post, this, true)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    fun bind (post: Post, onClickListener: (Post) -> Unit) {
        setOnClickListener {
            onClickListener(post)
        }
        title.text = post.title
        subreddit.text = post.subreddit
        posted_by.text = "Posted by ${post.author}"
        comments.text = post.numComments.toString()
        //posted_by.text = "Posted by ${post.author} - ${getTimeAgo(post.created)}"

        Glide.with(context)
            .load(post.thumbnail)
            .centerCrop()
            .into(thumbnail)
    }
}