package com.example.simpleinstagram.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.simpleinstagram.Post
import com.example.simpleinstagram.R
import com.parse.FindCallback
import com.parse.ParseException
import com.parse.ParseQuery
import com.parse.ParseUser


class ProfileFragment : HomeFragment() {


    override fun queryPosts(){
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
        query.include(Post.KEY_USER)
        query.limit = 20
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser())
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
                        for (post in objects) {
                            posts.add(post)
                            Log.e("CUSTOMA",Integer.toString(i++))
                        }
                        gotPosts = true;
                        postAdapter.notifyDataSetChanged()
                        Log.e("CUSTOMA", "finished adding posts")
                    }
                }
            }

        })
    }

}