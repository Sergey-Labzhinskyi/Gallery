package com.bignerdranch.gallery;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkClient {

    private static final String BASE_URL = "https://api.imgur.com/3/";
    private static Retrofit sRetrofit;



    public static Retrofit getRetrofitClient() {
        if (sRetrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .build();
            sRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return sRetrofit;
    }

    public  void uploadToImgur(final Context context, String filePath, final ProgressBar progressBar) {
        Retrofit retrofit = getRetrofitClient();
        UploadApi uploadApi = retrofit.create(UploadApi.class);

        Log.d("position", filePath);
        //create a file object using file path
        File file = new File(filePath);


        Call<GalleryItem> call = uploadApi.uploadImage("",
                "", "", "",
                MultipartBody.Part.createFormData("image",
                        file.getName(), RequestBody.create(MediaType.parse("image/*"), file)));

        call.enqueue(new Callback<GalleryItem>() {
            @Override
            public void onResponse(Call<GalleryItem> call, Response<GalleryItem> response) {
                if (response.code() == 200) {
                    progressBar.setVisibility(View.INVISIBLE);
                    if (response.body() != null) {
                        GalleryItem.urllists.add("http://imgur.com/" + response.body().getData().getId());
                    }
                }else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(context, "An unknown error has occured.", Toast.LENGTH_SHORT)
                            .show();
                }
                Log.d("onResponse", String.valueOf(response.code()));
                Log.d("urllists", String.valueOf(GalleryItem.urllists.size()));
            }
            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(context, "An unknown error has occured.", Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }
}
