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

/**
 * Created by monusurana on 7/24/16.
 */

import android.os.Bundle;
import android.widget.Toast;

import com.example.flicks.R;
import com.example.flicks.data.Movie;
import com.example.flicks.data.MovieClient;
import com.example.flicks.data.MovieInterface;
import com.example.flicks.data.Video;
import com.example.flicks.data.Videos;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class YoutubePlayerActivity extends YouTubeBaseActivity {

    public static final String YT_API_KEY = "AIzaSyCGRYEFc6hufdvE7XGpsD7mV3XrB_oJrlM";
    private static final String SAVED_MOVIE = "SAVED_MOVIE";

    private Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_youtube);

        if (savedInstanceState != null && savedInstanceState.containsKey(SAVED_MOVIE)) {
            mMovie = savedInstanceState.getParcelable(SAVED_MOVIE);
        } else {
            mMovie = getIntent().getParcelableExtra(MovieActivity.INTENT_MOVIE_DETAIL);
        }

        callRetrofitServer(mMovie);
    }

    private void startYoutubeVideo(final String key) {
        YouTubePlayerView youTubePlayerView =
                (YouTubePlayerView) findViewById(R.id.player);

        youTubePlayerView.initialize(YT_API_KEY,
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                        YouTubePlayer youTubePlayer, boolean b) {
                        youTubePlayer.loadVideo(key);
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                        YouTubeInitializationResult youTubeInitializationResult) {
                        Toast.makeText(YoutubePlayerActivity.this, R.string.youtube_failure, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVED_MOVIE, mMovie);
    }

    /**
     * Function to call Movie server to fetch Now Playing Movies
     */
    private void callRetrofitServer(Movie movie) {
        MovieInterface movieInterface = new MovieClient().getMovieInterface();
        Call<Videos> call = movieInterface.getVideos(movie.getId(), "en");

        call.enqueue(new Callback<Videos>() {
            @Override
            public void onResponse(Call<Videos> call, Response<Videos> response) {
                List<Video> videos = response.body().getVideos();

                if (videos.size() > 0)
                    startYoutubeVideo(videos.get(0).getKey());
            }

            @Override
            public void onFailure(Call<Videos> call, Throwable t) {
                Timber.d("Failure " + t.getLocalizedMessage());
            }
        });
    }
}

