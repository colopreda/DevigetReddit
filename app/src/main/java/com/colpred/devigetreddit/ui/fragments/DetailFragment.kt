package com.colpred.devigetreddit.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.colpred.devigetreddit.R
import com.colpred.devigetreddit.model.Post
import kotlinx.android.synthetic.main.fragment_detail.*

private const val POST = "post"

class DetailFragment : Fragment() {
    private var post: Post? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            post = it.getParcelable(POST)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUI()
    }

    private fun setUpUI() {
        title.text = post?.title
        author.text = post?.author
        subreddit.text = post?.subreddit
        Glide.with(this)
            .load(post?.url)
            .centerInside()
            .into(image)
    }

    companion object {
        @JvmStatic
        fun newInstance(post: Post) =
            DetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(POST, post)
                }
            }
    }
}