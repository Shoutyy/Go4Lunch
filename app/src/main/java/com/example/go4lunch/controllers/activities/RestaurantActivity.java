package com.example.go4lunch.controllers.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
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
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.api.UserHelper;
import com.example.go4lunch.models.detail.PlaceDetail;
import com.example.go4lunch.models.detail.PlaceResult;
import com.example.go4lunch.models.nerby_search.ResultSearch;
import com.example.go4lunch.models.User;
import com.example.go4lunch.utils.DatesAndHours;
import com.example.go4lunch.utils.FirebaseUtils;
import com.example.go4lunch.utils.PlaceStream;
import com.example.go4lunch.views.RestaurantAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

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
    private static final String SELECTED = "SELECTED";
    private static final String UNSELECTED = "UNSELECTED";
    String API_KEY = BuildConfig.MAPS_API_KEY;
    private String placeId;
    private String formattedPhoneNumber;
    private String url;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionUsers = db.collection("users");
    private RestaurantAdapter restaurantAdapter;


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
        floatingBtn();
        starBtn();
        setUpRecyclerView(placeId);
    }

    private void retrieveData() {
        placeId = getIntent().getStringExtra("placeId");
        this.executeHttpRequestWithRetrofit(placeId);

        if (placeId != null) {
            UserHelper.getUser(Objects.requireNonNull(FirebaseUtils.getCurrentUser()).getUid()).addOnSuccessListener(documentSnapshot -> {
                User user = documentSnapshot.toObject(User.class);
                if (user != null && user.getPlaceId() != null && user.getLike() != null) {
                    if (user.getPlaceId() != null && user.getPlaceId().contains(placeId)) {
                        mFloatingBtn.setImageDrawable(getResources().getDrawable(R.drawable.baseline_done_white_24));
                    }
                    if (user.getPlaceId() != null && user.getLike().contains(placeId)) {
                        UserHelper.updateLike(FirebaseUtils.getCurrentUser().getUid(), placeId);
                        mStarBtn.setAlpha(1);
                    }
                }
            });
        }
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

    public void floatingBtn() {
        mFloatingBtn.setOnClickListener(v -> {
            if (v.getId() == R.id.floating_ok_btn) {
                if (placeId != null) {
                    UserHelper.getUser(Objects.requireNonNull(FirebaseUtils.getCurrentUser()).getUid()).addOnSuccessListener(documentSnapshot -> {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            if (user.getPlaceId() != null && user.getPlaceId().contains(placeId)) {
                                UserHelper.deletePlaceId(Objects.requireNonNull(Objects.requireNonNull(FirebaseUtils.getCurrentUser()).getUid()));
                                mFloatingBtn.setImageDrawable(getResources().getDrawable(R.drawable.baseline_clear_black_24));
                            } else if (user.getPlaceId() == null || !user.getPlaceId().equals(placeId)) {
                                UserHelper.updatePlaceId(Objects.requireNonNull(FirebaseUtils.getCurrentUser()).getUid(), placeId, DatesAndHours.getCurrentTime());
                                mFloatingBtn.setImageDrawable(getResources().getDrawable(R.drawable.baseline_done_white_24));
                            }
                        }
                    });
                }
            }
        });
    }

    public void starBtn() {
        mStarBtn.setOnClickListener(v ->
                likeRestaurant());
    }

    public void likeRestaurant() {
        if (placeId != null) {
            UserHelper.getUser(Objects.requireNonNull(FirebaseUtils.getCurrentUser()).getUid()).addOnSuccessListener(documentSnapshot -> {
                User user = documentSnapshot.toObject(User.class);
                if (user != null) {
                    if (user.getLike() == null || !user.getLike().contains(placeId) ) {
                        UserHelper.updateLike(FirebaseUtils.getCurrentUser().getUid(), placeId);
                        mStarBtn.setAlpha(1);
                    } else if (user.getLike().contains(placeId)){
                        UserHelper.deleteLike(FirebaseUtils.getCurrentUser().getUid(), placeId);
                        mStarBtn.setAlpha(0.3f);
                    }
                }
            });
        }
    }

    private void setUpRecyclerView(String placeId) {

        Query query = collectionUsers.whereEqualTo("placeId", placeId);

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();
        this.restaurantAdapter = new RestaurantAdapter(options, Glide.with(this));
        mRecyclerViewRestaurant.setHasFixedSize(true);
        mRecyclerViewRestaurant.setAdapter(restaurantAdapter);
        mRecyclerViewRestaurant.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onStart() {
        super.onStart();
        restaurantAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        restaurantAdapter.stopListening();
    }

    /**
     * dispose subscription
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.disposeWhenDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    /**
     * dispose subscription
     */
    private void disposeWhenDestroy() {
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

}