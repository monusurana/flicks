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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.flicks.R;
import com.example.flicks.data.Crew;
import com.example.flicks.utils.RoundedTransformation;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CrewRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String IMAGE_URL = "https://image.tmdb.org/t/p/w185";
    private static final int TYPE_ITEM = 1;

    private List<Crew> mItems;

    /**
     * Constructor
     */
    public CrewRecyclerViewAdapter() {
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View itemView = inflater.inflate(R.layout.crew_rv_items, parent, false);

        return new ViewHolderItem(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Crew crew = mItems.get(position);

        ImageView photoView = ((ViewHolderItem) holder).ivPhoto;
        TextView nameView = ((ViewHolderItem) holder).tvName;
        TextView departmentView = ((ViewHolderItem) holder).tvDepartment;

        nameView.setText(crew.getName());
        departmentView.setText(crew.getDepartment());

        String Url = IMAGE_URL + crew.getProfilePath();

        Picasso.with(photoView.getContext()).load(Url)
                .noFade()
                .fit()
                .centerInside()
                .error(R.drawable.placeholder_face)
                .placeholder(R.drawable.placeholder_face)
                .transform(new RoundedTransformation())
                .into(((ViewHolderItem) holder).ivPhoto);
    }

    @Override
    public int getItemCount() {
        if (mItems != null)
            return mItems.size();

        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }

    /**
     * Function to update the list of TodoItems
     *
     * @param tasks List of TodoItems as an input
     */
    public void updateAdapter(List<Crew> tasks) {
        mItems = null;
        mItems = tasks;

        notifyDataSetChanged();
    }

    /**
     * View Holder for Recycler View Item
     */
    public class ViewHolderItem extends RecyclerView.ViewHolder {
        @BindView(R.id.ivPhoto)
        ImageView ivPhoto;
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvDepartment)
        TextView tvDepartment;

        public ViewHolderItem(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}