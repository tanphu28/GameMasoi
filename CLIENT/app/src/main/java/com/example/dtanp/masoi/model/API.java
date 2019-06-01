package com.example.dtanp.masoi.model;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface API {
    @GET
    Call<ResponseBody> downloadApk(@Url String url);
}
