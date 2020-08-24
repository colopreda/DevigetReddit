package com.colpred.devigetreddit.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.colpred.devigetreddit.R
import com.colpred.devigetreddit.model.Post
import com.himanshurawat.imageworker.Extension
import com.himanshurawat.imageworker.ImageWorker
import kotlinx.android.synthetic.main.fragment_detail.*

private const val POST = "post"

class DetailFragment : Fragment() {
    var post: Post? = null
    var imageBitmap: Bitmap? = null

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
            .asBitmap()
            .load(post?.url)
            .centerInside()
            .into(object : CustomTarget<Bitmap>(){
                override fun onLoadCleared(placeholder: Drawable?) {
                    // Nothing to do here
                }
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    image.setImageBitmap(resource)
                    imageBitmap = resource
                }

            })
        save.setOnClickListener {
            saveImageToExternalStorage()
        }
    }

    private fun saveImageToExternalStorage() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) -> {
                saveImage()
            }
            else -> {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
            }
        }
    }

    private fun saveImage() {
        ImageWorker.to(requireContext()).
        directory("DevigetReddit").
        setFileName(post?.title).
        withExtension(Extension.PNG).
        save(imageBitmap)
        Toast.makeText(context, "The image was saved successfully", Toast.LENGTH_LONG)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            100 -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    saveImage()
                } else {
                    // Tell why permission is needed
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
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