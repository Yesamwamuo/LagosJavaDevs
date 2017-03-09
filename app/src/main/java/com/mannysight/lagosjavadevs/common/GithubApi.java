package com.mannysight.lagosjavadevs.common;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by wamuo on 3/6/2017.
 */

public interface GithubApi {

    @GET("search/users")
    Call<LagosJavaDevelopers> getLagosJavaDevs(@Query(value = "q", encoded = true) String query, @Query("page") Integer page);
}
