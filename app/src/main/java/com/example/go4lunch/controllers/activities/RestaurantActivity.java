package com.example.go4lunch.controllers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.models.detail.PlaceDetail;
import com.example.go4lunch.models.detail.PlaceResult;
import com.example.go4lunch.models.nerby_search.ResultSearch;
import com.example.go4lunch.utils.PlaceStream;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RestaurantActivity extends AppCompatActivity {

    ImageView mRestaurantPhoto;
    TextView mRestaurantName;
    RatingBar mRating;
    TextView mRestaurantAddress;
    Button mCallBtn;
    Button mWebBtn;
    FloatingActionButton mFloatingBtn;
    RecyclerView mRecyclerViewRestaurant;
    Button mStarBtn;

    private PlaceResult placeResult;
    private Disposable disposable;
    private RequestManager mGlide;

    String API_KEY = BuildConfig.MAPS_API_KEY;
    private String placeId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        mRestaurantPhoto = findViewById(R.id.restaurant_photo);
        mRestaurantName = findViewById(R.id.restaurant_name);
        mRating = findViewById(R.id.rating_bar);
        mRestaurantAddress = findViewById(R.id.restaurant_address);
        mCallBtn = findViewById(R.id.call_btn);
        mWebBtn = findViewById(R.id.web_btn);
        mFloatingBtn = findViewById(R.id.floating_ok_btn);
        mRecyclerViewRestaurant = findViewById(R.id.restaurant_RV);
        mStarBtn = findViewById(R.id.star_btn);

        this.retrieveData();

    }

    private void retrieveData() {
        String placeId = getIntent().getStringExtra("placeId");
        this.executeHttpRequestWithRetrofit(placeId);
    }

    private void executeHttpRequestWithRetrofit(String placeId) {
        this.disposable = PlaceStream.streamFetchDetails(this.placeId)
                .subscribeWith(new DisposableObserver<PlaceDetail>() {


                    @Override
                    public void onNext(@NonNull PlaceDetail placeDetail) {
                        updateUI(placeDetail, mGlide);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    private void updateUI(PlaceDetail results, RequestManager glide) {
        mGlide = glide;

        //for add photos with Glide
        /*
        if (results.getPhotos() != null && !results.getPhotos().isEmpty()) {
            Glide.with(this)
                    .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&maxheight=400&photoreference=" + results.getPhotos().get(0).getPhotoReference() + "&key=" + API_KEY)
                    .apply(RequestOptions.centerCropTransform())
                    .into(mRestaurantPhoto);
        } else {
            mRestaurantPhoto.setImageResource(R.drawable.no_picture);
        }

         */
        //For Restaurant Name
        mRestaurantName.setText(results.getResult().getName());
        //For Restaurant address
        mRestaurantAddress.setText(results.getResult().getVicinity());
        //For rating
       // restaurantRating(results);
        //For  restaurant telephone number
       // String formattedPhoneNumber = placeResult.getFormattedPhoneNumber();
        //callBtn(formattedPhoneNumber);
        //For Website
       // String url = placeResult.getWebsite();
        //webBtn(url);
    }

    private void restaurantRating(ResultSearch results) {
        if (results.getRating() != null) {
            double restaurantRating = results.getRating();
            double rating = (restaurantRating / 5) * 3;
            this.mRating.setRating((float) rating);
            this.mRating.setVisibility(View.VISIBLE);
        } else {
            this.mRating.setVisibility(View.GONE);
        }
    }


}
