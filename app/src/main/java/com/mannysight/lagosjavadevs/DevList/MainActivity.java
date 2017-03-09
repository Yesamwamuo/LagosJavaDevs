package com.mannysight.lagosjavadevs.DevList;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.mannysight.lagosjavadevs.R;
import com.mannysight.lagosjavadevs.common.GithubApi;
import com.mannysight.lagosjavadevs.common.Item;
import com.mannysight.lagosjavadevs.common.LagosJavaDevelopers;
import com.mannysight.lagosjavadevs.common.Misc;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String BASE_URL = "https://api.github.com";
    private static final String ACCESS_TOKEN = "250b793982b19382ae09dfd3558429291c9d4970";
    private static final String DEV_LIST = "dev_list";
    private static final String TAG = MainActivity.class.getSimpleName();
    private GithubApi api;
    private DevListAdapter adapter;
    private static ArrayList<Item> lagosJavaDevsInAdapter;

    @BindView(R.id.collapsingToolBar)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @BindView(R.id.activity_main_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.activity_main_error_layout)
    LinearLayout errorLayout;

    @BindView(R.id.activity_main_progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.activity_main_retry_button)
    Button retryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        collapsingToolbarLayout.setTitle("Lagos Java Devs");
        retryButton.setOnClickListener(this);

        setupAdapter();
        setupRecyclerView();

        if (savedInstanceState != null) {
            LagosJavaDevelopers lagosJavaDevelopers = LagosJavaDevelopers.deserializeFromJson(savedInstanceState.getString(DEV_LIST));
            if (lagosJavaDevelopers != null) {
                refreshList((ArrayList<Item>) lagosJavaDevelopers.getItems());
            }
        } else {
            bootstrapRetrofit();
            loadDevs();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (lagosJavaDevsInAdapter.size() > 0) {
            LagosJavaDevelopers lagosJavaDevelopers = new LagosJavaDevelopers();
            lagosJavaDevelopers.setItems(lagosJavaDevsInAdapter);
            outState.putString(DEV_LIST, lagosJavaDevelopers.serializeToJson());
            super.onSaveInstanceState(outState);
        }
    }

    private void setupRecyclerView() {
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupAdapter() {
        adapter = new DevListAdapter(this);
        lagosJavaDevsInAdapter = adapter.getLagosJavaDevs();
    }

    private void bootstrapRetrofit() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                HttpUrl url = request.url().newBuilder().addQueryParameter(
                        "access_token",
                        ACCESS_TOKEN
                ).build();
                request = request.newBuilder().url(url).build();
                return chain.proceed(request);
            }
        }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(GithubApi.class);
    }

    private void loadDevs() {
        if (Misc.isOnline(getApplicationContext())) {
            showProgressBar();
            final ArrayList<Item> lagosJavaDevelopersItems = new ArrayList<>();
            final int lastPage = 7;

            // Loop through all pages (github api releases about 30 items per call)
            for (int pageNumber = 1; pageNumber <= lastPage; pageNumber++) {

                String query = "language:java+location:lagos";
                Call<LagosJavaDevelopers> call = api.getLagosJavaDevs(query, pageNumber);

                final int finalPageNumber = pageNumber;
                call.enqueue(new Callback<LagosJavaDevelopers>() {
                    @Override
                    public void onResponse(Call<LagosJavaDevelopers> call, Response<LagosJavaDevelopers> response) {

                        LagosJavaDevelopers lagosJavaDevelopers = response.body();

                        if (lagosJavaDevelopers != null) {
                            lagosJavaDevelopersItems.addAll(lagosJavaDevelopers.getItems());

                        }
                        if (finalPageNumber == lastPage) {
                            refreshList(lagosJavaDevelopersItems);
                        }

                    }

                    @Override
                    public void onFailure(Call<LagosJavaDevelopers> call, Throwable t) {
                        showErrorLayout();
                    }
                });
            }
        } else {
            showErrorLayout();
        }
    }

    private void showErrorLayout() {
        errorLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void showRecyclerView() {
        recyclerView.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
    }

    private void refreshList(ArrayList<Item> lagosJavaDevelopersItems) {
        if (lagosJavaDevelopersItems.size() != 0) {
            lagosJavaDevsInAdapter.clear();
            lagosJavaDevsInAdapter.addAll(lagosJavaDevelopersItems);
            adapter.notifyDataSetChanged();
            showRecyclerView();
        } else {
            showErrorLayout();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_main_retry_button:
                loadDevs();
                break;
        }
    }
}
