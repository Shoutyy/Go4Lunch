package com.example.go4lunch.views;

import android.graphics.Color;
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

import androidx.recyclerview.widget.RecyclerView;

public class ListViewHolder extends RecyclerView.ViewHolder {
    String API_KEY = BuildConfig.MAPS_API_KEY;

    TextView mName;
    TextView mAddress;
    TextView mOpenHours;
    ImageView mPhoto;
    RatingBar mRatingBar;
    TextView mDistance;
    TextView mWorkmates;

    public ListViewHolder(View itemView) {
        super(itemView);
        mName = itemView.findViewById(R.id.list_name);
        mAddress = itemView.findViewById(R.id.list_address);
        mOpenHours = itemView.findViewById(R.id.list_openHours);
        mPhoto = itemView.findViewById(R.id.list_photo);
        mRatingBar = itemView.findViewById(R.id.list_rating);
        mDistance = itemView.findViewById(R.id.list_distance);
        mWorkmates = itemView.findViewById(R.id.list_workMates);
    }

    public void updateWithData(ResultSearch results, RequestManager glide, String userLocation) {
        //restaurant name
        this.mName.setText(results.getName());

        //restaurant address
        this.mAddress.setText(results.getVicinity());

        //restaurant rating
        restaurantRating(results);
        /*
        //restaurant distance
        restaurantDistance(mPosition, results.getGeometry().getLocation());
        String distance = Math.round(distanceResults[0]) + "m";
        this.mDistance.setText(distance);
        Log.d("TestDistance", distance);

        //for numberWorkmates
        numberWorkmates(results.getPlaceId());

        //for retrieve opening hours (open or closed)
        if (results.getOpeningHours() != null) {

            if (results.getOpeningHours().getOpenNow().toString().equals("false")) {
                this.mOpenHours.setText(R.string.closed);
                this.mOpenHours.setTextColor(Color.RED);
            } else if (results.getOpeningHours().getOpenNow().toString().equals("true")) {
                getHoursInfo(results);
            }
        }
        if (results.getOpeningHours() == null) {
            this.mOpenHours.setText(R.string.opening_hours_not_available);
            this.mOpenHours.setTextColor(Color.BLACK);
        }

        //for add photos with Glide
        if (results.getPhotos() != null && !results.getPhotos().isEmpty()) {
            glide.load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&maxheight=400&photoreference=" + results.getPhotos().get(0).getPhotoReference() + "&key=" + API_KEY)
                    .apply(RequestOptions.circleCropTransform()).into(mPhoto);
        } else {
            mPhoto.setImageResource(R.drawable.no_picture);
        }

         */


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

}
