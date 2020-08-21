package com.colpred.devigetreddit.ui.fragments

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.colpred.devigetreddit.R
import com.colpred.devigetreddit.adapters.HomeAdapter
import com.colpred.devigetreddit.model.RedditJsonResponse
import com.colpred.devigetreddit.network.ApiHelperImpl
import com.colpred.devigetreddit.network.RetrofitBuilder
import com.colpred.devigetreddit.utils.Status
import com.colpred.devigetreddit.viewmodel.HomeViewModel
import com.github.stephenvinouze.advancedrecyclerview.pagination.extensions.enablePagination
import kotlinx.android.synthetic.main.home_fragment.*

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: HomeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpViewModel()
        setUpUI()
        setUpObservables()
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
        adapter = HomeAdapter()
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


}