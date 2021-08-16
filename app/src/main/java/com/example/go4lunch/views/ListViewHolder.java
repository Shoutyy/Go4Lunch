package com.example.go4lunch.views;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.models.nerby_search.ResultSearch;
import com.example.go4lunch.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;

import androidx.core.app.ActivityCompat;
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

        client = LocationServices.getFusedLocationProviderClient(itemView.getContext());

        /*
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

         */

    }

    public void updateWithData(ResultSearch results, RequestManager glide, String mPosition) {
        //restaurant name
        this.mName.setText(results.getName());

        //restaurant address
        this.mAddress.setText(results.getVicinity());

        getCurrentLocation(results);
        //restaurant rating
        restaurantRating(results);

        //restaurant distance


        /*
        //for numberWorkmates
        numberWorkmates(results.getPlaceId());
        */

        //for retrieve opening hours (open or closed)
        if (results.getOpeningHours() != null) {

            if (results.getOpeningHours().getOpenNow().toString().equals("false")) {
                this.mOpenHours.setText(R.string.closed);
                this.mOpenHours.setTextColor((ContextCompat.getColor(itemView.getContext(),
                        R.color.closed)));
            } else if (results.getOpeningHours().getOpenNow().toString().equals("true")) {
                this.mOpenHours.setText(R.string.open);
                this.mOpenHours.setTextColor((ContextCompat.getColor(itemView.getContext(),
                        R.color.open)));
            }
        }

        if (results.getOpeningHours() == null) {
            this.mOpenHours.setText(R.string.opening_hours_not_available);
            this.mOpenHours.setTextColor(Color.BLACK);
        }

        //for add photos with Glide
        if (results.getPhotos() != null && !results.getPhotos().isEmpty()) {
            glide.load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&maxheight=400&photoreference=" + results.getPhotos().get(0).getPhotoReference() + "&key=" + API_KEY)
                    .into(mPhoto);
        } else {
            mPhoto.setImageResource(R.drawable.no_picture);
        }
    }


    private void getCurrentLocation(ResultSearch results) {
        @SuppressWarnings({"ResourceType"}) Task<Location> task = client.getLastLocation();
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

    /**
     * For rating
     *
     * @param results
     */
    private void restaurantRating(ResultSearch results) {
        if (results.getRating() != null) {
            double restaurantRating = results.getRating();
            double rating = (restaurantRating / 5) * 3;
            this.mRatingBar.setRating((float) rating);
            this.mRatingBar.setVisibility(View.VISIBLE);

        } else {
            this.mRatingBar.setVisibility(View.GONE);
        }
    }

    /**
     * For calculate restaurant distance
     *
     * @param startLocation
     * @param endLocation
     */
    private void restaurantDistance(String startLocation, com.example.go4lunch.models.nerby_search.Location endLocation) {
        String[] separatedStart = startLocation.split(",");
        double startLatitude = Double.parseDouble(separatedStart[0]);
        double startLongitude = Double.parseDouble(separatedStart[1]);
        double endLatitude = endLocation.getLat();
        double endLongitude = endLocation.getLng();
        android.location.Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, distanceResults);
    }

    /*
    /**
     * For retrieve number workmates who choose restaurant
     *
     * @param placeId
     */
    /*
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
                            mWormates.setText(workmatesNumber);


                        } else {
                            Log.e("numberMatesError", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

     */


}
