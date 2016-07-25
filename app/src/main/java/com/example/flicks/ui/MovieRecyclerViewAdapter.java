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

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.flicks.R;
import com.example.flicks.data.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String IMAGE_URL_PORTRAIT = "https://image.tmdb.org/t/p/w185";
    public static final String IMAGE_URL_LANDSCAPE = "https://image.tmdb.org/t/p/w300";
    private static final int TYPE_DEFAULT = 0;
    private static final int TYPE_HIGH_RATING = 1;

    private int mOrientation;
    private List<Movie> mItems;
    private OnItemClickListener mListener;

    /**
     * Constructor
     */
    public MovieRecyclerViewAdapter(int orientation) {
        mOrientation = orientation;
    }

    /**
     * Interface to get Edit and Delete events in the activity
     */
    public interface OnItemClickListener {
        void onItemClick(Movie item, View parent);

        void playYoutubeVideo(Movie item, View parent);
    }

    /**
     * Function to set the listener for Edit and Delete events
     *
     * @param listener Listener for the events
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View itemView;

        if (viewType == TYPE_DEFAULT) {
            itemView = inflater.inflate(R.layout.movie_rv_listitem, parent, false);
            return new ViewHolderItem(itemView);
        } else if (viewType == TYPE_HIGH_RATING) {
            itemView = inflater.inflate(R.layout.movie_rv_listitem_high, parent, false);
            return new ViewHolderHighItem(itemView);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Movie movie = mItems.get(position);

        if (holder instanceof ViewHolderItem) {
            ImageView posterView = ((ViewHolderItem) holder).ivPoster;
            TextView titleView = ((ViewHolderItem) holder).tvTitle;

            titleView.setText(movie.getTitle());

            TextView overviewView = ((ViewHolderItem) holder).tvOverview;
            overviewView.setText(movie.getOverview());

            if (mOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                String Url = IMAGE_URL_LANDSCAPE;
                Url += movie.getBackdropPath();

                Picasso.with(posterView.getContext()).load(Url)
                        .noFade()
                        .resize(300, 0)
                        .placeholder(R.drawable.placeholder_movie_landscape)
                        .error(R.drawable.placeholder_movie_landscape)
                        .into(((ViewHolderItem) holder).ivPoster);
            } else {
                String Url = IMAGE_URL_PORTRAIT;
                Url += movie.getPosterPath();


                Picasso.with(posterView.getContext()).load(Url)
                        .resize(131, 181)
                        .noFade()
                        .placeholder(R.drawable.placeholder_movie)
                        .error(R.drawable.placeholder_movie)
                        .into(((ViewHolderItem) holder).ivPoster);
            }
        } else {

            TextView titleView = ((ViewHolderHighItem) holder).tvTitle;
            titleView.setText(movie.getTitle());

            ImageView posterView = ((ViewHolderHighItem) holder).ivPoster;
            String UrlPoster = IMAGE_URL_PORTRAIT;
            UrlPoster += movie.getPosterPath();

            Picasso.with(posterView.getContext()).load(UrlPoster)
                    .resize(130, 181)
                    .noFade()
                    .placeholder(R.drawable.placeholder_movie)
                    .error(R.drawable.placeholder_movie)
                    .into(((ViewHolderHighItem) holder).ivPoster);

            ImageView backDropView = ((ViewHolderHighItem) holder).ivBackdrop;

            String UrlBackdrop = IMAGE_URL_LANDSCAPE;
            UrlBackdrop += movie.getBackdropPath();
            Picasso.with(backDropView.getContext()).load(UrlBackdrop)
                    .noFade()
                    .resize(0, 181)
                    .placeholder(R.drawable.placeholder_movie_landscape)
                    .error(R.drawable.placeholder_movie_landscape)
                    .into(((ViewHolderHighItem) holder).ivBackdrop);
        }
    }

    @Override
    public int getItemCount() {
        if (mItems != null)
            return mItems.size();

        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (mOrientation == Configuration.ORIENTATION_PORTRAIT && mItems.get(position).getVoteAverage() > 5.0)
            return TYPE_HIGH_RATING;
        else
            return TYPE_DEFAULT;
    }

    /**
     * Function to update the list of TodoItems
     *
     * @param tasks List of TodoItems as an input
     */
    public void updateAdapter(List<Movie> tasks) {
        mItems = null;
        mItems = tasks;

        notifyDataSetChanged();
    }

    /**
     * View Holder for Recycler View Item
     */
    public class ViewHolderItem extends RecyclerView.ViewHolder {
        @BindView(R.id.ivPoster)
        ImageView ivPoster;
        @BindView(R.id.tvTitle)
        TextView tvTitle;
        @BindView(R.id.tvOverview)
        TextView tvOverview;

        public ViewHolderItem(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null)
                        mListener.onItemClick(mItems.get(getLayoutPosition()), v);
                }
            });
        }
    }

    /**
     * View Holder for Recycler View high rating Item
     */
    public class ViewHolderHighItem extends RecyclerView.ViewHolder {
        @BindView(R.id.ivPoster)
        ImageView ivPoster;
        @BindView(R.id.tvTitle)
        TextView tvTitle;
        @BindView(R.id.ivBackdrop)
        ImageView ivBackdrop;
        @BindView(R.id.flBackdrop)
        FrameLayout flBackdrop;

        public ViewHolderHighItem(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            flBackdrop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null)
                        mListener.playYoutubeVideo(mItems.get(getLayoutPosition()), v);
                }
            });

            ivPoster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null)
                        mListener.onItemClick(mItems.get(getLayoutPosition()), v);
                }
            });
        }
    }
}