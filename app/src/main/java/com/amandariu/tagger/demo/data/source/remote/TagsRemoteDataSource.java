package com.amandariu.tagger.demo.data.source.remote;

import android.support.annotation.NonNull;
import android.util.Log;

import com.amandariu.tagger.R;
import com.amandariu.tagger.demo.TaggerApplication;
import com.amandariu.tagger.demo.data.Tag;
import com.amandariu.tagger.demo.data.source.ITagsDataSource;
import com.amandariu.tagger.demo.utils.HttpUtils;
import com.github.aurae.retrofit2.LoganSquareConverterFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;

/**
 * Gateway for working with the remote API to load tags. Defines the remote API endpoint and exposes
 * methods for fetching tags.
 *
 * @author Amanda Riu
 */
public class TagsRemoteDataSource implements ITagsDataSource {

    private static final String TAG = TagsRemoteDataSource.class.getSimpleName();
    private static final String BASE_URL = "https://gist.githubusercontent.com";

    private TagsRemoteApi mApi;


    interface TagsRemoteApi {
        @GET("/jgritman/7f2e89d1937ba9d9fc678f4c9844cbf1/raw/729eecaacbe749fbeeb891cc430d55235aa8036a/tags.json")
        Call<List<Tag>> getTags();
    }

    /**
     * Singleton. Initialize the HttpClient and build the api.
     */
    public TagsRemoteDataSource() {
        Log.v(TAG, "Initializing the Remote Tag Datasource");
        //
        // Singleton.
        // Creates a custom OkHttpClient that trusts all certificates
        OkHttpClient.Builder clientBuilder = HttpUtils.getTrustAllOkHttpClientBuilder();
        clientBuilder.connectTimeout(30, TimeUnit.SECONDS);
        clientBuilder.readTimeout(60, TimeUnit.SECONDS);
        clientBuilder.addNetworkInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                Request newRequest;
                HttpUrl originalHttpUrl = originalRequest.url();

                HttpUrl url = originalHttpUrl.newBuilder()
                        .build();

                newRequest = originalRequest.newBuilder()
                        .addHeader(
                                HttpUtils.HeaderContracts.HEADER_CONTENT_TYPE,
                                "application/json")
                        .url(url)
                        .build();
                return chain.proceed(newRequest);
            }
        });
        //
        // Create our retrofit ContentApi
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(LoganSquareConverterFactory.create())
                .client(clientBuilder.build())
                .build();

        mApi = retrofit.create(TagsRemoteApi.class);
    }


    /**
     * Asynchronously load tags from the remote API.
     *
     * @param callback The callback to send the loaded tags to.
     */
    @Override
    public void getTags(@NonNull final ILoadTagsCallback callback) {
        Log.d(TAG, "Processing request to get tags from the remote API");
        final long startTime = System.nanoTime();

        Call<List<Tag>> call = mApi.getTags();
        call.enqueue(new Callback<List<Tag>>() {
            @Override
            public void onResponse(Call<List<Tag>> call, Response<List<Tag>> response) {
                if (response.isSuccessful()) {
                    List<Tag> tags = response.body();
                    callback.onTagsLoaded(tags);
                } else {
                    Log.e(TAG, "Error getting tags from remote API: " + response.errorBody());
                    try {
                        final String msg = response.errorBody().string();
                        callback.onDataNotAvailable(msg);
                    } catch (IOException io) {
                        callback.onDataNotAvailable(
                                TaggerApplication.getInstance().getString(R.string.error_remote_api));
                    }
                    long elapsedTime = System.nanoTime() - startTime;
                    Log.d(TAG, "Total time to pull tags from remote API ["
                            + TimeUnit.NANOSECONDS.toSeconds(elapsedTime) + "] seconds");
                }
            }

            @Override
            public void onFailure(Call<List<Tag>> call, Throwable t) {
                Log.e(TAG, "Error getting tags from remote API: " + t.getMessage());
                callback.onDataNotAvailable(t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void saveTags(@NonNull List<Tag> tags, @NonNull ISaveTagsCallback callback) {
        // do nothing
    }

    @Override
    public void shutdown() {
        Log.v(TAG, "Shutting down the remote tags datasource");
        mApi = null;
    }
}