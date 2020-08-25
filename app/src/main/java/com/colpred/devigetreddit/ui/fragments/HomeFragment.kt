package com.colpred.devigetreddit.ui.fragments

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.colpred.devigetreddit.R
import com.colpred.devigetreddit.adapters.HomeAdapter
import com.colpred.devigetreddit.db.DatabaseBuilder
import com.colpred.devigetreddit.db.DatabaseHelperImpl
import com.colpred.devigetreddit.model.ChildrenData
import com.colpred.devigetreddit.model.Post
import com.colpred.devigetreddit.network.ApiHelperImpl
import com.colpred.devigetreddit.network.RetrofitBuilder
import com.colpred.devigetreddit.ui.DetailActivity
import com.colpred.devigetreddit.utils.Status
import com.colpred.devigetreddit.viewmodel.HomeViewModel
import com.github.stephenvinouze.advancedrecyclerview.pagination.extensions.enablePagination
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(), HomeAdapter.PostItemListener {

    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: HomeAdapter

    private var dualPane: Boolean = false
    private var shouldLoad: Boolean = true
    private lateinit var currentPost: Post

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpViewModel()
        checkLandscape(savedInstanceState)
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
        viewModel.markAsRead(curPost.id)
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
                    recycler_view.smoothScrollToPosition(0)
                    shimmer_view_container.visibility = View.GONE
                    shimmer_view_container.stopShimmerAnimation()
                    swipetorefresh.visibility = View.VISIBLE
                }
                Status.LOADING -> {
                    shimmer_view_container.visibility = View.VISIBLE
                    shimmer_view_container.startShimmerAnimation()
                    swipetorefresh.visibility = View.GONE
                }
                Status.ERROR -> {
                    //Handle Error
                    shimmer_view_container.visibility = View.GONE
                    shimmer_view_container.stopShimmerAnimation()
                    swipetorefresh.visibility = View.VISIBLE
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    swipetorefresh.isRefreshing = false
                }
            }
        })
    }

    private fun renderList(posts: MutableList<ChildrenData>) {
        adapter.items = posts.map { it.data }.toMutableList()
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
                !viewModel.hasMoreItems() || !shouldLoad
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
            shouldLoad = true
            viewModel.fetchPosts()
        }
        fab.setOnClickListener {
            shouldLoad = false
            viewModel.deleteAllPosts()
        }
    }

    private fun setUpViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            viewModelFactory { HomeViewModel(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(requireContext()))
            )}
        ).get(HomeViewModel::class.java)
    }

    override fun onClickedPost(post: Post) {
        showPost(post)
    }

    override fun onDeleteClicked(post: Post, position: Int) {
        adapter.removeItem(position)
        viewModel.deletePost(post.id)
    }


}