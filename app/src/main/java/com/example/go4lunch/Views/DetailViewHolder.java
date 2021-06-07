package com.example.go4lunch.Views;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.models.User;



public class DetailViewHolder extends RecyclerView.ViewHolder {
    ImageView mImageView;
    TextView mTextView;

    public DetailViewHolder(View itemView) {
        super(itemView);
        mImageView = itemView.findViewById(R.id.detail_main_picture);
        mTextView = itemView.findViewById(R.id.detail_textview_username);
    }


    public void updateWithData(User results){
        RequestManager glide = Glide.with(itemView);
        if (!(results.getUrlPicture() == null)){
            glide.load(results.getUrlPicture()).apply(RequestOptions.circleCropTransform()).into(mImageView);
        }else{
            glide.load(R.drawable.ic_anon_user_48dp).apply(RequestOptions.circleCropTransform()).into(mImageView);
        }

        this.mTextView.setText(itemView.getResources().getString(R.string.restaurant_detail_recyclerview, results.getUsername()));
        this.changeTextColor(R.color.black);
    }

    private void changeTextColor(int color){
        int mColor = itemView.getContext().getResources().getColor(color);
        this.mTextView.setTextColor(mColor);
    }
}
