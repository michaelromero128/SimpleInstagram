package com.example.simpleinstagram

import android.app.Application
import com.parse.Parse
import com.parse.ParseObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class ParseApplication : Application(){
    override fun onCreate(){
        super.onCreate()
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG)
        val builder = OkHttpClient.Builder()
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        builder.networkInterceptors().add(httpLoggingInterceptor)
        ParseObject.registerSubclass(Post::class.java)
        Parse.initialize(Parse.Configuration.Builder(this)
            .applicationId(getString(R.string.appId))
            .clientKey(getString(R.string.clientKey))
            .server(getString(R.string.parseURL)).build())
    }
}