/*
 * Copyright 2016 Monu Surana
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.flicks.data;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by monusurana on 7/20/16.
 */
public class MovieClient {

    private static final String SERVER_URL = "https://api.themoviedb.org/3/";
    private static final String API_KEY = "0634e04af2d31142f100771861ab7f39";
    private static final String PARAM_API_KEY = "api_key";
    private static final String GSON_DATE_FORMAT = "yyyy-MM-dd";

    private Retrofit mRetrofit;
    private OkHttpClient mOkHttpClient;

    protected Retrofit getRetrofit() {
        if (mRetrofit == null) {
            Gson gson = new GsonBuilder()
                    .setDateFormat(GSON_DATE_FORMAT)
                    .create();

            mRetrofit = new Retrofit.Builder()
                    .baseUrl(SERVER_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(getHttpClient())
                    .build();
        }

        return mRetrofit;
    }

    @NonNull
    private OkHttpClient getHttpClient() {
        if (mOkHttpClient == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            httpClient.addInterceptor(new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    HttpUrl originalHttpUrl = original.url();

                    HttpUrl url = originalHttpUrl.newBuilder()
                            .setEncodedQueryParameter(PARAM_API_KEY, API_KEY)
                            .build();

                    Request.Builder requestBuilder = original.newBuilder()
                            .url(url);

                    Request request = requestBuilder.build();

                    return chain.proceed(request);
                }
            });

            mOkHttpClient = httpClient.build();
        }

        return mOkHttpClient;
    }

    public MovieInterface getMovieInterface() {
        return getRetrofit().create(MovieInterface.class);
    }
}
