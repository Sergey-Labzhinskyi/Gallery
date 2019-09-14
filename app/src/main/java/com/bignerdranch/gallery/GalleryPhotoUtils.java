package com.bignerdranch.gallery;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class GalleryPhotoUtils {

    private static final String DIRECTORY_DCIM = "/DCIM/Camera";

    public static final String CAMERA_IMAGE_BUCKET_NAME = Environment.getExternalStorageDirectory().toString() + DIRECTORY_DCIM;

    // get id of image bucket from path
    public static String getBucketId(String path) {
        return String.valueOf(path.toLowerCase().hashCode());
    }

    //get images
    public static List<GalleryItem> getImages(Context context) {
        final String[] projection = {MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA};
        final String selection = MediaStore.Images.Media.BUCKET_ID + " = ?";
        final String[] selectionArgs = {GalleryPhotoUtils.getBucketId(CAMERA_IMAGE_BUCKET_NAME)};
        final Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);
        ArrayList<GalleryItem> result = new ArrayList<>(cursor.getCount());
        if (cursor.moveToFirst()) {
            final int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            final int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            do {
                GalleryItem galleryItem = new GalleryItem(cursor.getString(dataColumn), cursor.getString(nameColumn));
                result.add(galleryItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
        Log.d("COUNT", String.valueOf(result.size()));
        return result;

    }
}
