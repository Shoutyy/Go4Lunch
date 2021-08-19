package com.example.go4lunch.controllers.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

    private static final int REQUEST_CALL = 100;
    String API_KEY = BuildConfig.MAPS_API_KEY;
    private String placeId;
    private String formattedPhoneNumber;
    private String url;

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

        retrieveData();

    }

    private void retrieveData() {
        String placeId = getIntent().getStringExtra("placeId");
        this.executeHttpRequestWithRetrofit(placeId);
    }

    private void executeHttpRequestWithRetrofit(String placeId) {
        this.disposable = PlaceStream.streamFetchDetails(placeId)
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

        if (results.getResult().getPhotos() != null && !results.getResult().getPhotos().isEmpty()) {
            Glide.with(this)
                    .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&maxheight=400&photoreference=" + results.getResult().getPhotos().get(0).getPhotoReference() + "&key=" + API_KEY)
                    .apply(RequestOptions.centerCropTransform())
                    .into(mRestaurantPhoto);
        } else {
            mRestaurantPhoto.setImageResource(R.drawable.no_picture);
        }

        mRestaurantName.setText(results.getResult().getName());
        mRestaurantAddress.setText(results.getResult().getVicinity());

        restaurantRating(results);

        formattedPhoneNumber = results.getResult().getFormattedPhoneNumber();
        callBtn(formattedPhoneNumber);

        url = results.getResult().getWebsite();
        webBtn(url);
    }

    private void restaurantRating(PlaceDetail results) {
        if (results.getResult().getRating() != null) {
            double restaurantRating = results.getResult().getRating();
            double rating = (restaurantRating / 5) * 3;
            this.mRating.setRating((float) rating);
            this.mRating.setVisibility(View.VISIBLE);
        } else {
            this.mRating.setVisibility(View.GONE);
        }
    }

    public void callBtn(String formattedPhoneNumber) {
        mCallBtn.setOnClickListener(view -> makePhoneCall(formattedPhoneNumber));
    }

    private void makePhoneCall(String formattedPhoneNumber) {

        if (ContextCompat.checkSelfPermission(RestaurantActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RestaurantActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else if (formattedPhoneNumber != null && !formattedPhoneNumber.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + formattedPhoneNumber));
            Log.d("PhoneNumber", formattedPhoneNumber);
            startActivity(intent);
        } else {
            Toast.makeText(RestaurantActivity.this, getString(R.string.no_phone_available), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall(formattedPhoneNumber);
            } else {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void webBtn(String url) {
        mWebBtn.setOnClickListener(view -> makeWebView(url));
    }

    public void makeWebView(String url) {
        if (url != null && !url.isEmpty()) {
            Intent intent = new Intent(RestaurantActivity.this, WebViewActivity.class);
            intent.putExtra("website", url);
            Log.d("Website", url);
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.no_website, Toast.LENGTH_SHORT).show();
        }
    }

}
