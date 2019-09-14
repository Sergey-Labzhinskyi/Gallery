package com.bignerdranch.gallery;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface UploadApi {

    String CLIENT_ID = "Authorization: Client-ID 0a9cb313572e112";

    @Multipart
    @Headers({CLIENT_ID})
    @POST("image")
    Call<GalleryItem> uploadImage( @Query("title") String title,
                                   @Query("description") String description,
                                   @Query("album") String albumId,
                                   @Query("account_url") String username,
                                   @Part MultipartBody.Part file
    );
}
