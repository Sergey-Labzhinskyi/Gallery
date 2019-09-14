package com.bignerdranch.gallery;


import java.util.ArrayList;
import java.util.List;


public class GalleryItem {
    private String imageUri;
    private String imageName;
    private UploadedImage data;
    public static List<String> urllists = new ArrayList<>();




    public GalleryItem(String imageUri, String imageName) {
        this.imageUri = imageUri;
        this.imageName = imageName;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }



    public UploadedImage getData() {
        return data;
    }

    public void setData(UploadedImage data) {
        this.data = data;
    }



    public  class UploadedImage{

        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }



        @Override public String toString() {
            return "UploadedImage{" +
                    "id='" + id + '\'' +
                    '}';
        }
    }
}
