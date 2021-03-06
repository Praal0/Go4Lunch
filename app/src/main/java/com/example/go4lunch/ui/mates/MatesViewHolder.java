package com.example.go4lunch.ui.mates;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.api.RestaurantsHelper;
import com.example.go4lunch.models.User;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MatesViewHolder extends RecyclerView.ViewHolder {

    ImageView mImageView;
    TextView mTextView;

    public MatesViewHolder(@NonNull View itemView) {
        super(itemView);
        mImageView = itemView.findViewById(R.id.mates_main_picture);
        mTextView = itemView.findViewById(R.id.mates_textview_username);
    }


    public void updateWithData(User results){
        RequestManager glide = Glide.with(itemView);
        if (!(results.getUrlPicture() == null)){
            glide.load(results.getUrlPicture()).apply(RequestOptions.circleCropTransform()).into(mImageView);
        }else{
            glide.load(R.drawable.ic_no_image_available).apply(RequestOptions.circleCropTransform()).into(mImageView);
        }

        RestaurantsHelper.getBooking(results.getUid(), getTodayDate()).addOnCompleteListener(restaurantTask -> {
            if (restaurantTask.isSuccessful()){
                if (restaurantTask.getResult().size() == 1){ // User already booked a restaurant today
                    for (QueryDocumentSnapshot restaurant : restaurantTask.getResult()) {
                        this.mTextView.setText(results.getUsername() + " " +itemView.getResources().getString(R.string.mates_is_eating_at) +" "+ restaurant.getData().get("restaurantName"));
                        this.changeTextColor(R.color.black);
                    }
                }else{ // No restaurant booked for this user today
                    this.mTextView.setText(results.getUsername() + " " +itemView.getResources().getString(R.string.mates_hasnt_decided));
                    this.changeTextColor(R.color.colorGray);
                }
            }
        });
    }

    private void changeTextColor(int color){
        int mColor = itemView.getContext().getResources().getColor(color);
        this.mTextView.setTextColor(mColor);
    }

    private String getTodayDate(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        return df.format(c.getTime());
    }
}
