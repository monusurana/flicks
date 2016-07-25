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

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by monusurana on 7/20/16.
 */
public interface MovieInterface {
    @GET("movie/now_playing")
    Call<NowPlayingMovies> getNowPlayingMovies(@Query("page") Integer page, @Query("language") String language);

    @GET("movie/{id}/credits")
    Call<Credits> getCredits(@Path("id") int id);

    @GET("movie/{id}/videos")
    Call<Videos> getVideos(@Path("id") int id, @Query("language") String language);
}
