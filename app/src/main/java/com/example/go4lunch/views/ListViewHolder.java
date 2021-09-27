package com.example.go4lunch.views;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.api.UserHelper;
import com.example.go4lunch.models.detail.PlaceResult;
import com.example.go4lunch.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class ListViewHolder extends RecyclerView.ViewHolder  {
    String API_KEY = BuildConfig.MAPS_API_KEY;
    private float[] distanceResults = new float[3];
    FusedLocationProviderClient client;

    TextView mName;
    TextView mAddress;
    TextView mOpenHours;
    ImageView mPhoto;
    RatingBar mRatingBar;
    TextView mDistance;
    TextView mWorkmates;
    TextView mWorkmatesNumber;
    double currentLat = 0, currentLong = 0;
    String mPosition = currentLat + "," + currentLong;

    public ListViewHolder(View itemView) {
        super(itemView);
        mName = itemView.findViewById(R.id.list_name);
        mAddress = itemView.findViewById(R.id.list_address);
        mOpenHours = itemView.findViewById(R.id.list_openHours);
        mPhoto = itemView.findViewById(R.id.list_photo);
        mRatingBar = itemView.findViewById(R.id.list_rating);
        mDistance = itemView.findViewById(R.id.list_distance);
        mWorkmates = itemView.findViewById(R.id.list_workMates);
        mWorkmatesNumber = itemView.findViewById(R.id.list_workMatesNumber);

        client = LocationServices.getFusedLocationProviderClient(itemView.getContext());
    }

    public void updateWithData(PlaceResult result, RequestManager glide, String mPosition) {

        this.mName.setText(result.getName());

        this.mAddress.setText(result.getVicinity());

        getCurrentLocation(result);

        restaurantRating(result);

        numberWorkmates(result.getPlaceId());

        if (result.getOpeningHours() != null) {

            if (result.getOpeningHours().getOpenNow().toString().equals("false")) {
                this.mOpenHours.setText(R.string.closed);
                this.mOpenHours.setTextColor((ContextCompat.getColor(itemView.getContext(),
                        R.color.closed)));
            } else if (result.getOpeningHours().getOpenNow().toString().equals("true")) {
                this.mOpenHours.setText(R.string.open);
                this.mOpenHours.setTextColor((ContextCompat.getColor(itemView.getContext(),
                        R.color.open)));
            }
        }

        if (result.getOpeningHours() == null) {
            this.mOpenHours.setText(R.string.opening_hours_not_available);
            this.mOpenHours.setTextColor(Color.BLACK);
        }

        if (result.getPhotos() != null && !result.getPhotos().isEmpty()) {
            glide.load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&maxheight=400&photoreference=" + result.getPhotos().get(0).getPhotoReference() + "&key=" + API_KEY)
                    .into(mPhoto);
        } else {
            mPhoto.setImageResource(R.drawable.no_picture);
        }
    }


    private void getCurrentLocation(PlaceResult results) {
        @SuppressLint("MissingPermission") @SuppressWarnings({"ResourceType"}) Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                currentLat = location.getLatitude();
                currentLong = location.getLongitude();
                mPosition =  currentLat + "," + currentLong;
                restaurantDistance(mPosition, results.getGeometry().getLocation());
                String distance = Math.round(distanceResults[0]) + "m";
                this.mDistance.setText(distance);
                Log.d("TestDistance", distance);
            }
        });
    }

    private void restaurantRating(PlaceResult results) {
        if (results.getRating() != null) {
            double restaurantRating = results.getRating();
            double rating = (restaurantRating / 5) * 3;
            this.mRatingBar.setRating((float) rating);
            this.mRatingBar.setVisibility(View.VISIBLE);

        } else {
            this.mRatingBar.setVisibility(View.GONE);
        }
    }

    private void restaurantDistance(String startLocation, com.example.go4lunch.models.detail.Location endLocation) {
        String[] separatedStart = startLocation.split(",");
        double startLatitude = Double.parseDouble(separatedStart[0]);
        double startLongitude = Double.parseDouble(separatedStart[1]);
        double endLatitude = endLocation.getLat();
        double endLongitude = endLocation.getLng();
        android.location.Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, distanceResults);
    }

    private void numberWorkmates(String placeId) {

        UserHelper.getUsersCollection()
                .whereEqualTo("placeId", placeId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                                Log.d("numberWorkmates", documentSnapshot.getId() + " " + documentSnapshot.getData());
                            }
                            int numberWorkmates = Objects.requireNonNull(task.getResult()).size();
                            String workmatesNumber = "(" + numberWorkmates + ")";
                            mWorkmatesNumber.setText(workmatesNumber);
                        } else {
                            Log.e("numberMatesError", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
