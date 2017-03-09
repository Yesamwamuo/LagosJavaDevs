package com.mannysight.lagosjavadevs.DevList;

/**
 * Created by wamuo on 3/6/2017.
 */

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mannysight.lagosjavadevs.DevProfile.DevProfileActivity;
import com.mannysight.lagosjavadevs.R;
import com.mannysight.lagosjavadevs.common.Item;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DevListAdapter extends RecyclerView.Adapter<DevListAdapter.DevListViewHolder> {

    private ArrayList<Item> lagosJavaDevs;
    private Context context;

    public DevListAdapter(Context context) {
        this.context = context;
        lagosJavaDevs = new ArrayList<>();
    }

    @Override
    public DevListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.dev_list_row, parent, false);

        return new DevListViewHolder(itemView, context);
    }

    @Override
    public void onBindViewHolder(DevListViewHolder holder, int position) {

        Item lagosJavaDev = lagosJavaDevs.get(position);
        holder.populate(context, lagosJavaDev);
    }


    public ArrayList<Item> getLagosJavaDevs() {
        return lagosJavaDevs;
    }

    @Override
    public int getItemCount() {
        return lagosJavaDevs.size();
    }

    public class DevListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_view_dev)
        ImageView devImage;

        @BindView(R.id.text_view_user_name)
        TextView devUsername;

        @BindView(R.id.dev_list_row_progressBar)
        ProgressBar progressBar;

        @BindView(R.id.dev_list_row_layout)
        LinearLayout layout;


        public DevListViewHolder(View itemView, final Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = DevProfileActivity.newIntent(context, lagosJavaDevs.get(getAdapterPosition()));

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                        Pair[] pair = new Pair[2];

                        pair[0] = new Pair<View, String>(devImage, "image");
                        pair[1] = new Pair<View, String>(devUsername, "userName");

                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pair);
                        context.startActivity(intent, options.toBundle());

                    } else {
                        context.startActivity(intent);
                    }
                }
            });
        }


        private void populate(Context context, Item lagosJavaDev) {

            devUsername.setText(lagosJavaDev.getLogin());

            Picasso.with(context)
                    .load(lagosJavaDev.getAvatarUrl())
                    .fit()
                    .placeholder(R.drawable.github_avatar)
                    .into(devImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }

    }


}