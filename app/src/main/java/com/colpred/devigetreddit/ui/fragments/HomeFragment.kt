package com.colpred.devigetreddit.ui.fragments

import android.content.Intent
import android.content.res.Configuration
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.colpred.devigetreddit.R
import com.colpred.devigetreddit.adapters.HomeAdapter
import com.colpred.devigetreddit.model.Post
import com.colpred.devigetreddit.model.RedditJsonResponse
import com.colpred.devigetreddit.network.ApiHelperImpl
import com.colpred.devigetreddit.network.RetrofitBuilder
import com.colpred.devigetreddit.ui.DetailActivity
import com.colpred.devigetreddit.utils.Status
import com.colpred.devigetreddit.viewmodel.HomeViewModel
import com.github.stephenvinouze.advancedrecyclerview.pagination.extensions.enablePagination
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.home_fragment.*

class HomeFragment : Fragment(), HomeAdapter.PostItemListener {

    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: HomeAdapter

    private var dualPane: Boolean = false
    private lateinit var currentPost: Post

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        checkLandscape(savedInstanceState)
        setUpViewModel()
        setUpUI()
        setUpObservables()
    }

    private fun checkLandscape(savedInstanceState: Bundle?) {
        dualPane = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        currentPost = savedInstanceState?.getParcelable("curPost") ?: Post()
        if (dualPane) {
            showPost(currentPost)
        }
    }

    private fun showPost(curPost: Post) {
        currentPost = curPost
        if (dualPane) {
            var postFragment = childFragmentManager.findFragmentById(R.id.action_charactersFragment_to_characterDetailFragment) as? DetailFragment
            if (postFragment?.post?.id != curPost.id) {
                postFragment = DetailFragment.newInstance(curPost)

                parentFragmentManager.beginTransaction().apply {
                    replace(R.id.detail, postFragment)
                    commit()
                }
            }
        } else {
            val intent = Intent().apply {
                setClass(requireContext(), DetailActivity::class.java)
                putExtra("post", curPost)
            }
            startActivity(intent)
        }
    }

    protected inline fun <VM : ViewModel> viewModelFactory(crossinline f: () -> VM) =
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(aClass: Class<T>):T = f() as T
        }

    private fun setUpObservables() {
        viewModel.getPosts().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { posts -> renderList(posts) }
                    swipetorefresh.isRefreshing = false
                    adapter.isLoading = false
                }
                Status.LOADING -> {

                }
                Status.ERROR -> {
                    //Handle Error
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    swipetorefresh.isRefreshing = false
                }
            }
        })
    }

    private fun renderList(posts: RedditJsonResponse) {
        adapter.items.addAll(posts.data.children.map { it.data }.toMutableList())
        adapter.notifyDataSetChanged()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("curPost", currentPost)
    }

    private fun setUpUI() {
        recycler_view.layoutManager = LinearLayoutManager(context)
        recycler_view.enablePagination(
            isLoading = {
                adapter.isLoading
            },
            hasAllItems = {
                !viewModel.hasMoreItems()
            },
            onLoad = {
                adapter.isLoading = true
                viewModel.loadMorePosts()
            }
        )
        adapter = HomeAdapter(this)
        recycler_view.addItemDecoration(
            DividerItemDecoration(
                recycler_view.context,
                (recycler_view.layoutManager as LinearLayoutManager).orientation
            )
        )
        recycler_view.adapter = adapter
        swipetorefresh.setOnRefreshListener {
            adapter.items.clear()
            adapter.notifyDataSetChanged()
            viewModel.fetchPosts()
        }
    }

    private fun setUpViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            viewModelFactory { HomeViewModel(ApiHelperImpl(RetrofitBuilder.apiService)) }
        ).get(HomeViewModel::class.java)
    }

    override fun onClickedPost(post: Post) {
        showPost(post)
    }


}