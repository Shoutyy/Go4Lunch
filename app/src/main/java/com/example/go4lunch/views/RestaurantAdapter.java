package com.example.go4lunch.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bumptech.glide.RequestManager;
import com.example.go4lunch.R;
import com.firebase.ui.auth.data.model.User;

public class RestaurantAdapter {

    private RequestManager glide;

    /**
     * Create constructor
     * @param glide
     */
    public RestaurantAdapter(RequestManager glide) {
        this.glide = glide;
    }


    @NonNull

    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_restaurant_item, parent, false);
        return new RestaurantViewHolder(view);
    }


    protected void onBindViewHolder(@NonNull RestaurantViewHolder restaurantViewHolder, int position, @NonNull User model) {
        restaurantViewHolder.updateWithData(model, this.glide);
    }
}
