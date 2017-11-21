package com.amandariu.tagger.demo.data.source.remote

import android.util.Log

import com.amandariu.tagger.ITag
import com.amandariu.tagger.demo.R
import com.amandariu.tagger.demo.TaggerApplication
import com.amandariu.tagger.demo.data.Tag
import com.amandariu.tagger.demo.data.source.ISourceBase
import com.amandariu.tagger.demo.data.source.ITagsDataSource
import com.amandariu.tagger.demo.utils.HttpUtils
import com.github.aurae.retrofit2.LoganSquareConverterFactory

import java.io.IOException
import java.util.concurrent.TimeUnit

import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET

/**
 * Gateway for working with the remote API to load tags. Defines the remote API endpoint and exposes
 * methods for fetching tags.
 *
 * @author Amanda Riu
 */
class TagsRemoteDataSource : ITagsDataSource {

    companion object {
        private val TAG = TagsRemoteDataSource::class.java.simpleName
        private val BASE_URL = "https://gist.githubusercontent.com"
    }

    internal interface TagsRemoteApi {
        @get:GET("/jgritman/7f2e89d1937ba9d9fc678f4c9844cbf1/raw/729eecaacbe749fbeeb891cc430d55235aa8036a/tags.json")
        val tags: Call<List<Tag>>
    }

    private var mApi: TagsRemoteApi? = null

    /**
     * Singleton. Initialize the HttpClient and build the api.
     */
    init {
        Log.v(TAG, "Initializing the Remote Tag Datasource")
        //
        // Singleton.
        // Creates a custom OkHttpClient that trusts all certificates
        val clientBuilder = HttpUtils.getTrustAllOkHttpClientBuilder()
        clientBuilder.connectTimeout(30, TimeUnit.SECONDS)
        clientBuilder.readTimeout(60, TimeUnit.SECONDS)
        clientBuilder.addNetworkInterceptor { chain ->
            val originalRequest = chain.request()
            val newRequest: Request
            val originalHttpUrl = originalRequest.url()

            val url = originalHttpUrl.newBuilder()
                    .build()

            newRequest = originalRequest.newBuilder()
                    .addHeader(
                            HttpUtils.HeaderContracts.HEADER_CONTENT_TYPE,
                            "application/json")
                    .url(url)
                    .build()
            chain.proceed(newRequest)
        }
        //
        // Create our retrofit ContentApi
        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(LoganSquareConverterFactory.create())
                .client(clientBuilder.build())
                .build()

        mApi = retrofit.create(TagsRemoteApi::class.java)
    }

    /**
     * Asynchronously load tags from the remote API.
     *
     * @param callback The callback to send the loaded tags to.
     */
    override fun getTags(callback: ISourceBase.ILoadTagsCallback) {
        Log.d(TAG, "Processing request to get tags from the remote API")
        val startTime = System.nanoTime()

        val call = mApi!!.tags
        call.enqueue(object : Callback<List<Tag>> {
            override fun onResponse(call: Call<List<Tag>>, response: Response<List<Tag>>) {
                if (response.isSuccessful) {
                    val tags = response.body()
                    tags?.let {
                        callback.onTagsLoaded(tags)
                    }
                } else {
                    Log.e(TAG, "Error getting tags from remote API: " + response.errorBody()!!)
                    try {
                        val msg = response.errorBody()!!.string()
                        callback.onDataNotAvailable(msg)
                    } catch (io: IOException) {
                        callback.onDataNotAvailable(
                                TaggerApplication.getInstance().getString(R.string.error_remote_api))
                    }

                    val elapsedTime = System.nanoTime() - startTime
                    Log.d(TAG, "Total time to pull tags from remote API ["
                            + TimeUnit.NANOSECONDS.toSeconds(elapsedTime) + "] seconds")
                }
            }

            override fun onFailure(call: Call<List<Tag>>, t: Throwable) {
                Log.e(TAG, "Error getting tags from remote API: " + t.message)
                callback.onDataNotAvailable(t.localizedMessage)
            }
        })
    }

    override fun saveTags(tags: List<ITag>, callback: ITagsDataSource.ISaveTagsCallback) {
        // do nothing
    }

    override fun shutdown() {
        Log.v(TAG, "Shutting down the remote tags datasource")
        mApi = null
    }
}