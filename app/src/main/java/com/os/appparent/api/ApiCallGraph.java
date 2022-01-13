package com.os.appparent.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.os.appparent.model.DriveItem;
import com.os.appparent.model.FolderDrive;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiCallGraph {
    String BASE_URL = "https://graph.microsoft.com/v1.0/me/";
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    ApiCallGraph apiCallGraph = new Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson)).build().create(ApiCallGraph.class);

    @Headers({"Content-Type: text/plain"})
    @PUT("drive/root:/{path}:/content")
    Call<ResponseBody> writeOneDrive(@Header("Authorization") String authorization, @Body RequestBody body, @Path("path") String path);

    @GET("drive/root:/{path}:/children")
    Call<FolderDrive> getFolder(@Header("Authorization") String authorization, @Path("path") String path);

    @GET("drive/root:/{path}")
    Call<DriveItem> getDriveItem(@Header("Authorization") String authorization, @Path("path") String path);

    @DELETE("drive/root:/{path}/flag")
    Call<ResponseBody> deleteFlag(@Header("Authorization") String authorization,@Path("path") String path);

    @Headers({"Content-Type: application/json"})
    @POST("drive/root:/{path}:/children")
    Call<ResponseBody> createFlag(@Header("Authorization") String authorization, @Body RequestBody text, @Path("path") String path);

}
