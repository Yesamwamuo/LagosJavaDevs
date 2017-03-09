package com.mannysight.lagosjavadevs.DevProfile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mannysight.lagosjavadevs.R;
import com.mannysight.lagosjavadevs.common.Item;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DevProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String EXTRA_ITEM_STRING = "EXTRA_ITEM_STRING";

    @BindView(R.id.activity_dev_profile_image_view)
    ImageView devImage;

    @BindView(R.id.activity_dev_profile_username)
    TextView devUsername;

    @BindView(R.id.activity_dev_profile_profile_url)
    TextView profileUrl;

    @BindView(R.id.activity_dev_profile_progressBar)
    ProgressBar progressBar;

    @BindView(R.id.activity_dev_profile_share_button)
    Button shareButton;

    @BindView(R.id.activity_dev_profile_banner)
    LinearLayout bannerLayout;

    Item lagosJavaDev;

    public static Intent newIntent(Context context, Item lagosJavaDev) {

        Intent intent = new Intent(context, DevProfileActivity.class);
        intent.putExtra(EXTRA_ITEM_STRING, lagosJavaDev.serializeToJson(lagosJavaDev));
        return intent;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Enable Transitions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_profile);
        ButterKnife.bind(this);

        String stringItem = getIntent().getStringExtra(EXTRA_ITEM_STRING);
        lagosJavaDev = Item.deserializeFromJson(stringItem);
        populate(lagosJavaDev);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_dev_profile_profile_url:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(lagosJavaDev.getHtmlUrl()));
                startActivity(browserIntent);
                break;
            case R.id.activity_dev_profile_share_button:

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Check out this awesome developer @" + lagosJavaDev.getLogin() + ", " + lagosJavaDev.getHtmlUrl();
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Lagos Java Developer");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                break;
        }
    }

    private void populate(Item lagosJavaDev) {

        devUsername.setText(lagosJavaDev.getLogin());

        profileUrl.setText(lagosJavaDev.getHtmlUrl());
        profileUrl.setOnClickListener(this);

        shareButton.setOnClickListener(this);

        Picasso.with(getApplicationContext())
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
