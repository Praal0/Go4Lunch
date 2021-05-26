package com.example.go4lunch.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.go4lunch.R;
import com.example.go4lunch.Utils.PlacesStreams;
import com.example.go4lunch.base.BaseActivity;
import com.example.go4lunch.fragment.MapFragment;
import com.example.go4lunch.models.PlacesInfo.PlacesDetails.PlaceDetailsInfo;
import com.example.go4lunch.models.PlacesInfo.PlacesDetails.PlaceDetailsResults;
import com.example.go4lunch.models.User;
import com.glide.slider.library.SliderLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class PlaceDetailActivity extends BaseActivity implements View.OnClickListener {

    TextView mRestaurantName;
    TextView mRestaurantAddress;
    RecyclerView mRestaurantRecyclerView;
    SliderLayout mDemoSlider;
    FloatingActionButton mFloatingActionButton;
    Button mButtonCall;
    Button mButtonLike;
    Button mButtonWebsite;
    RatingBar mRatingBar;

    private static final double MAX_RATING = 5;
    private static final double MAX_STAR = 3;

    private Disposable mDisposable;
    private PlaceDetailsResults requestResult;

    private List<User> mDetailUsers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);
        initItem();
    }

    private void initItem() {
        mRestaurantName = findViewById(R.id.restaurant_name);
        mRestaurantAddress = findViewById(R.id.restaurant_address);
        mRestaurantRecyclerView = findViewById(R.id.restaurant_recyclerView);
        mDemoSlider = findViewById(R.id.slider);
        mFloatingActionButton = findViewById(R.id.floatingActionButton);
        mButtonCall = findViewById(R.id.restaurant_item_call);
        mButtonLike = findViewById(R.id.restaurant_item_like);
        mButtonWebsite = findViewById(R.id.restaurant_item_website);
        mRatingBar = findViewById(R.id.item_ratingBar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }

    }

    // -------------------
    // HTTP (RxJAVA)
    // -------------------

    private void executeHttpRequestWithRetrofit(String placeId){
        this.mDisposable = PlacesStreams.streamSimpleFetchPlaceInfo(placeId, MapFragment.API_KEY).subscribeWith(createObserver());
    }

    private <T> DisposableObserver<T> createObserver(){
        return new DisposableObserver<T>() {
            @Override
            public void onNext(T t) {
                if (t instanceof PlaceDetailsInfo) {
                    requestResult = ((PlaceDetailsInfo) t).getResult();
                    updateUI(((PlaceDetailsInfo) t));
                }else{
                    Log.e("TAG", "onNext: " + t.getClass() );
                }
            }
            @Override
            public void onError(Throwable e) {onError(e);}
            @Override
            public void onComplete() {}
        };
    }

    // -------------------
    // UPDATE UI
    // -------------------

    private void updateUI(PlaceDetailsInfo results){
        if (results != null){
            if (getCurrentUser() != null){
            }else{

            }
            mRestaurantName.setText(results.getResult().getName());
            mRestaurantAddress.setText(results.getResult().getVicinity());
            this.displayRating(results);
        }
    }

    private void displayFAB(int icon, int color){
        Drawable mDrawable = ContextCompat.getDrawable(getBaseContext(), icon).mutate();
        mDrawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        mFloatingActionButton.setImageDrawable(mDrawable);
    }

    private void displayRating(PlaceDetailsInfo results){
        if (results.getResult().getRating() != null){
            double googleRating = results.getResult().getRating();
            double rating = googleRating / MAX_RATING * MAX_STAR;
            this.mRatingBar.setRating((float)rating);
            this.mRatingBar.setVisibility(View.VISIBLE);
        }else{
            this.mRatingBar.setVisibility(View.GONE);
        }
    }
}