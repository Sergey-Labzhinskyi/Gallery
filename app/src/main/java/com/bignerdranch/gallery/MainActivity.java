package com.bignerdranch.gallery;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.reactivestreams.Subscription;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements GalleryRecyclerViewAdapter.GalleryAdapterCallBacks {

    public List<GalleryItem> mGalleryItems;
    private static final int RC_READ_STORAGE = 5;
    RecyclerView mRecyclerView;
    GalleryRecyclerViewAdapter mGalleryAdapter;
    public static final String URL = "URL";
    private static final String PREF = "Preferences";
    private int mSpinnerInitCount = 0;
    private static final int NO_OF_EVENTS = 1;
    private Subscription mSubscription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("Gallery", "MAINACTIVITY");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewGallery);

        getListFromLocal(PREF);
        setSpinner();
        setOrientation();
        createRWAdapter();
    }

    private void setOrientation(){
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        }
    }

    private void createRWAdapter(){
        //Create RecyclerView Adapter
        mGalleryAdapter = new GalleryRecyclerViewAdapter(this);
        //set adapter to RecyclerView
        mRecyclerView.setAdapter(mGalleryAdapter);
        //check for read storage permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //Get images
            mGalleryItems = GalleryPhotoUtils.getImages(this);
            Log.d("Size", String.valueOf(mGalleryItems.size()));
            // add images to gallery recyclerview using adapter
            mGalleryAdapter.addGalleryItems(mGalleryItems);
        } else {
            //request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RC_READ_STORAGE);
        }
    }


    @Override
    public void onItemSelected(String position, ProgressBar progressBar) {
            NetworkClient networkClient = new NetworkClient();
                    networkClient.uploadToImgur(this, position, progressBar);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_READ_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Get images
                mGalleryItems = GalleryPhotoUtils.getImages(this);
                // add images to gallery recyclerview using adapter
                mGalleryAdapter.addGalleryItems(mGalleryItems);
            } else {
                Toast.makeText(this, "Storage Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void setSpinner() {

        Spinner spinner = findViewById(R.id.spinner);

        ArrayAdapter adapter =
                new ArrayAdapter(this,  android.R.layout.simple_spinner_dropdown_item,
                        GalleryItem.urllists);
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter <String> adapter1 = new ArrayAdapter <String> (
                this,
                android.R.layout.simple_spinner_item,
                GalleryItem.urllists);

        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter1);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, final long id) {
                Log.d("id", String.valueOf(GalleryItem.urllists.get((int) id)));

                if (mSpinnerInitCount < NO_OF_EVENTS) {
                    mSpinnerInitCount++;
                } else {
                    webViewStart(GalleryItem.urllists.get((int) id));
                }
              /*  Observable<String> observable = Observable.fromCallable(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return GalleryItem.urllists.get((int) id);
                    }
                });

                    observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<String>() {


                            @Override
                            public void onError(Throwable e) {
                            }

                            @Override
                            public void onComplete() {

                            }

                            @Override
                            public void onSubscribe(Disposable d) {

                            }


                            @Override
                            public void onNext(String string) {
                                webViewStart(GalleryItem.urllists.get((int) id));
                            }
                        });
            }*/
        }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    private  void webViewStart(String url){
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra(URL, url);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveListInLocal(GalleryItem.urllists,PREF);
    }

    public void saveListInLocal(List<String> list, String key) {

        SharedPreferences prefs = getSharedPreferences("Gallery", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    public ArrayList<String> getListFromLocal(String key) {

        if (GalleryItem.urllists.size() == 0) {
            GalleryItem.urllists.add("links");
        }
        SharedPreferences prefs = getSharedPreferences("Gallery", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }
}
