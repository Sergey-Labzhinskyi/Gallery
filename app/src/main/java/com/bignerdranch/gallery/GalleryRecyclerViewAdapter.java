package com.bignerdranch.gallery;

import android.content.Context;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



public class GalleryRecyclerViewAdapter extends RecyclerView.Adapter {

    private List<GalleryItem> mGalleryItems;
    private Context mContext;
    private GalleryAdapterCallBacks mAdapterCallBacks;



    public GalleryRecyclerViewAdapter(Context context) {
        this.mContext = context;
        this.mAdapterCallBacks = (GalleryAdapterCallBacks) context;
        this.mGalleryItems = new ArrayList<>();
    }


    //add new gallery items to RecyclerView
    public void addGalleryItems(List<GalleryItem> galleryItems) {
        int previousSize = this.mGalleryItems.size();
        this.mGalleryItems.addAll(galleryItems);
        notifyItemRangeInserted(previousSize, galleryItems.size());

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View row = inflater.inflate(R.layout.gallery_photo_item, parent, false);
        return new GalleryPhotoItemHolder(row);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        //get current gallery Item
        final GalleryItem currentItem = mGalleryItems.get(position);
        //create file to load with Picasso
        File imageViewThoumb = new File(currentItem.getImageUri());
        //cast holder with gallery holder
        final GalleryPhotoItemHolder galleryPhotoItemHolder = (GalleryPhotoItemHolder) holder;
        //Load with Picasso
        Picasso.get()
                .load(imageViewThoumb)
                .into(galleryPhotoItemHolder.mImageView);

        galleryPhotoItemHolder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapterCallBacks.onItemSelected(currentItem.getImageUri(), galleryPhotoItemHolder.mProgressBar);
                   galleryPhotoItemHolder.mProgressBar.setVisibility(View.VISIBLE);

                Log.d("onClick" , currentItem.getImageUri());
            }
        });

    }

    @Override
    public int getItemCount() {
        return mGalleryItems.size();
    }

    public class GalleryPhotoItemHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        ProgressBar mProgressBar;


        public GalleryPhotoItemHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.image_view);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            mProgressBar.setVisibility(View.INVISIBLE);

        }
    }


    //Interface for communication of Adapter and MainActivity
    public interface GalleryAdapterCallBacks {
        void onItemSelected(String position, ProgressBar progressBar);
    }
}
