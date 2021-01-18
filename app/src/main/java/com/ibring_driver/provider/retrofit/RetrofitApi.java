package com.ibring_driver.provider.retrofit;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Url;

public interface RetrofitApi
{


    @GET
    Call<ResponseBody> callGet(@Url String url);

    @POST
    Call<ResponseBody> callPost(@Url String url, @Body RequestBody body);

    @FormUrlEncoded
    @POST
    Call<ResponseBody> callPost1(@Url String url, @FieldMap HashMap<String, String> map);

    @POST
    Call<ResponseBody> callPostttt(@Url String url, @PartMap HashMap<String, RequestBody> map);


    @Multipart
    @POST
    Call<ResponseBody> callPostNew(@Url String url, @PartMap HashMap<String, RequestBody> map);

    @Multipart
    @POST
    Call<ResponseBody> callMultipart(@Url String url, @PartMap HashMap<String, RequestBody> map, @Part MultipartBody.Part part);



    @POST("vendor-service")
    Call<ResponseBody> callPostNewwwww(@Body JSONObject body);

    @POST
    @Multipart
    Call<ResponseBody> callMultipartList(@Url String url, @PartMap HashMap<String, RequestBody> map, @Part ArrayList<MultipartBody.Part> parts);

}


