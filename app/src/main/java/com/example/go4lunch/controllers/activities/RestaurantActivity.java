package com.example.go4lunch.controllers.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

    String API_KEY = BuildConfig.MAPS_API_KEY;

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

    }
}
