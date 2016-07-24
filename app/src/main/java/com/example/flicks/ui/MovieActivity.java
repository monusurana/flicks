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

package com.example.flicks.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.flicks.R;
import com.example.flicks.data.Movie;
import com.example.flicks.data.MovieClient;
import com.example.flicks.data.MovieInterface;
import com.example.flicks.data.NowPlayingMovies;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class MovieActivity extends AppCompatActivity implements MovieRecyclerViewAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String SAVED_MOVIES = "SAVED_MOVIES";
    public static final String INTENT_MOVIE_DETAIL = "MOVIE_DETAIL";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tasks_recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private MovieRecyclerViewAdapter mMovieRecyclerViewAdapter;
    private List<Movie> mMovies = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        ButterKnife.bind(this);

        initToolbar();
        setupRecyclerView();

        if (savedInstanceState != null && savedInstanceState.containsKey(SAVED_MOVIES)) {
            mMovies = savedInstanceState.getParcelableArrayList(SAVED_MOVIES);
            mMovieRecyclerViewAdapter.updateAdapter(mMovies);
        } else {
            callRetrofitServer();
        }

        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setOnRefreshListener(this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mMovies != null) {
            outState.putParcelableArrayList(SAVED_MOVIES, (ArrayList<? extends Parcelable>) mMovies);
        }
    }

    /**
     * Function to setup Recycler View
     */
    private void setupRecyclerView() {
        mMovieRecyclerViewAdapter = new MovieRecyclerViewAdapter(getResources().getConfiguration().orientation);

        mRecyclerView.setAdapter(mMovieRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mMovieRecyclerViewAdapter.setOnItemClickListener(this);
    }

    /**
     * Function to call Movie server to fetch Now Playing Movies
     */
    private void callRetrofitServer() {
        MovieInterface movieInterface = new MovieClient().getMovieInterface();
        Call<NowPlayingMovies> call = movieInterface.getNowPlayingMovies(1, "en");

        call.enqueue(new Callback<NowPlayingMovies>() {
            @Override
            public void onResponse(Call<NowPlayingMovies> call, Response<NowPlayingMovies> response) {
                List<Movie> movie = response.body().getMovies();

                mMovies.clear();
                mMovies.addAll(movie);
                mMovieRecyclerViewAdapter.updateAdapter(mMovies);
            }

            @Override
            public void onFailure(Call<NowPlayingMovies> call, Throwable t) {
                Timber.d("Failure " + t.getLocalizedMessage());
            }
        });
    }

    /**
     * Functon to initialize Toolbar
     */
    private void initToolbar() {
        //final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    /**
     * RecyclerView OnClick
     *
     * @param item Item
     * @param parent Parent
     */
    @Override
    public void onItemClick(Movie item, View parent) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(INTENT_MOVIE_DETAIL, item);
        startActivity(intent);
    }

    /**
     * Callback function for SwipeRefreshLayout
     */
    @Override
    public void onRefresh() {
        callRetrofitServer();
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
