package com.example.go4lunch.Views;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.models.User;

public class UserViewHolder extends RecyclerView.ViewHolder{

    ImageView mImageView;
    TextView mTextView;

    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        mImageView = itemView.findViewById(R.id.user_main_picture);
        mTextView = itemView.findViewById(R.id.user_textview_username);
    }

    public void updateWithData(User results) {
        RequestManager glide = Glide.with(itemView);
        if (!(results.getUrlPicture() == null)){
            glide.load(results.getUrlPicture()).apply(RequestOptions.circleCropTransform()).into(mImageView);
        }else{
            glide.load(R.drawable.ic_no_image_available).apply(RequestOptions.circleCropTransform()).into(mImageView);
        }
        mTextView.setText(results.getUsername());
    }
}
