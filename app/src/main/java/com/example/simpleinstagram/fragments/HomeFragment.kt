package com.example.simpleinstagram.fragments

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.simpleinstagram.Post
import com.example.simpleinstagram.PostAdapter
import com.example.simpleinstagram.R
import com.parse.FindCallback
import com.parse.ParseException
import com.parse.ParseQuery


open class HomeFragment : Fragment() {
    lateinit var rvPosts: RecyclerView
    var gotPosts: Boolean = false
    lateinit var postAdapter: PostAdapter
    var posts: MutableList<Post> = mutableListOf()
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view:View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvPosts = view.findViewById<RecyclerView>(R.id.rvPosts)
        postAdapter = PostAdapter(requireContext(), posts)
        rvPosts.adapter = postAdapter
        rvPosts.layoutManager = LinearLayoutManager(requireContext())
        swipeRefreshLayout =view.findViewById(R.id.swipeContainer)
        swipeRefreshLayout.setOnRefreshListener { queryPosts() }
        val resources: Resources = getResources();
        swipeRefreshLayout.setColorSchemeColors(
            resources.getColor(android.R.color.holo_blue_bright),
            resources.getColor(android.R.color.holo_green_light),
            resources.getColor(android.R.color.holo_orange_light),
            resources.getColor(android.R.color.holo_red_light))
        queryPosts()

    }
    open fun queryPosts(){
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
        query.include(Post.KEY_USER)
        query.limit = 20
        query.addDescendingOrder("createdAt")
        query.findInBackground(object: FindCallback<Post> {
            override fun done(objects: MutableList<Post>?, e: ParseException?) {
                Log.e("CUSTOMA","query finished");
                if(e != null){
                    Log.e("CUSTOMA","Error fetching posts")
                }else{
                    if( objects != null) {
                        Log.e("CUSTOMA", "adding posts")
                        var  i = 0;
                        postAdapter.clear();
                        postAdapter.addAll(objects)
                        gotPosts = true;
                        postAdapter.notifyDataSetChanged()
                        swipeRefreshLayout.setRefreshing(false)
                        Log.e("CUSTOMA", "finished adding posts")
                    }
                }
            }

        })
    }
}