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
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.flicks.R;
import com.example.flicks.data.Cast;
import com.example.flicks.data.Credits;
import com.example.flicks.data.Crew;
import com.example.flicks.data.Movie;
import com.example.flicks.data.MovieClient;
import com.example.flicks.data.MovieInterface;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

public class MovieDetailActivity extends AppCompatActivity {
    public static final String IMAGE_URL = "https://image.tmdb.org/t/p/w300";
    private static final String SAVED_CAST = "SAVED_CAST";
    private static final String SAVED_CREW = "SAVED_CREW";
    private static final String SAVED_MOVIE = "SAVED_MOVIE";

    @BindView(R.id.image)
    ImageView mImage;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.tvMovieTitle)
    TextView mMovieTitle;
    @BindView(R.id.tvMovieDate)
    TextView mMovieDate;
    @BindView(R.id.ratingBar)
    RatingBar mRatingBar;
    @BindView(R.id.tvMovieOverview)
    TextView mMovieOverview;
    @BindView(R.id.rvCasts)
    RecyclerView mCastsRecyclerView;
    @BindView(R.id.rvCrew)
    RecyclerView mCrewRecyclerView;

    @BindColor(android.R.color.transparent)
    int mTransparent;
    @BindColor(R.color.colorPrimaryDark)
    int mPrimaryDark;
    @BindColor(R.color.colorPrimary)
    int mColorPrimary;

    private CastRecyclerViewAdapter mCastsRecyclerViewAdapter;
    private CrewRecyclerViewAdapter mCrewRecyclerViewAdapter;

    private Movie mMovie;
    private List<Cast> mCast = new ArrayList<>();
    private List<Crew> mCrew = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        initToolbar();

        if (savedInstanceState != null && savedInstanceState.containsKey(SAVED_MOVIE)) {
            mMovie = savedInstanceState.getParcelable(SAVED_MOVIE);

        } else {
            mMovie = getIntent().getParcelableExtra(MovieActivity.INTENT_MOVIE_DETAIL);
        }

        String Url = IMAGE_URL + mMovie.getBackdropPath();

        mCollapsingToolbarLayout.setTitle(mMovie.getTitle());
        mCollapsingToolbarLayout.setExpandedTitleColor(mTransparent);

        Picasso.with(mImage.getContext()).load(Url).into(mImage, new Callback() {
            @Override
            public void onSuccess() {
                Bitmap bitmap = ((BitmapDrawable) mImage.getDrawable()).getBitmap();
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    public void onGenerated(Palette palette) {
                        applyPalette(palette);
                    }
                });
            }

            @Override
            public void onError() {

            }
        });

        mCastsRecyclerViewAdapter = new CastRecyclerViewAdapter();
        mCastsRecyclerView.setAdapter(mCastsRecyclerViewAdapter);
        mCastsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        mCrewRecyclerViewAdapter = new CrewRecyclerViewAdapter();
        mCrewRecyclerView.setAdapter(mCrewRecyclerViewAdapter);
        mCrewRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        if (savedInstanceState != null && savedInstanceState.containsKey(SAVED_CAST)) {
            mCast = savedInstanceState.getParcelableArrayList(SAVED_CAST);
            mCrew = savedInstanceState.getParcelableArrayList(SAVED_CREW);

            mCastsRecyclerViewAdapter.updateAdapter(mCast);
            mCrewRecyclerViewAdapter.updateAdapter(mCrew);
        } else {
            callRetrofitServer(mMovie);
        }

        mMovieTitle.setText(mMovie.getTitle());
        mMovieDate.setText(getYear(mMovie.getReleaseDate()));
        mMovieOverview.setText(mMovie.getOverview());
        mRatingBar.setRating(mMovie.getVoteAverage().floatValue());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mCast != null) {
            outState.putParcelableArrayList(SAVED_CAST, (ArrayList<? extends Parcelable>) mCast);
        }

        if (mCrew != null) {
            outState.putParcelableArrayList(SAVED_CREW, (ArrayList<? extends Parcelable>) mCrew);
        }

        outState.putParcelable(SAVED_MOVIE, mMovie);
    }

    /**
     * Helper function to extract year from date
     * @param date Date from year needs to extracted
     * @return Year extracted from date
     */
    private String getYear(String date) {
        return "(" + date.split("-")[0] + ")";
    }

    /**
     * Function to call Movie server to fetch Credits
     */
    private void callRetrofitServer(Movie item) {
        MovieInterface movieInterface = new MovieClient().getMovieInterface();
        Call<Credits> call = movieInterface.getCredits(item.getId());

        call.enqueue(new retrofit2.Callback<Credits>() {
            @Override
            public void onResponse(Call<Credits> call, Response<Credits> response) {
                List<Cast> cast = response.body().getCast();
                mCast.addAll(cast);
                mCastsRecyclerViewAdapter.updateAdapter(mCast);

                List<Crew> crew = response.body().getCrew();
                mCrew.addAll(crew);
                mCrewRecyclerViewAdapter.updateAdapter(mCrew);
            }

            @Override
            public void onFailure(Call<Credits> call, Throwable t) {

            }
        });
    }

    /**
     * Functon to initialize Toolbar
     */
    private void initToolbar() {
        setSupportActionBar(mToolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Function to update the Toolbar with Image Palette
     *
     * @param palette Palette
     */
    private void applyPalette(Palette palette) {
        mCollapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(mColorPrimary));
        mCollapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(mPrimaryDark));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(palette.getDarkMutedColor(mPrimaryDark));
        }
    }

    /**
     * Fab Icon onClick event handler
     * @param fabButon
     */
    @OnClick(R.id.fab)
    public void playVideo(FloatingActionButton fabButon) {
        Intent intent = new Intent(this, YoutubePlayerActivity.class);
        intent.putExtra(MovieActivity.INTENT_MOVIE_DETAIL, mMovie);
        startActivity(intent);
    }
}
