package com.example.go4lunch.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.RequestManager;
import com.example.go4lunch.R;
import com.example.go4lunch.models.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class RestaurantAdapter extends FirestoreRecyclerAdapter<User, RestaurantViewHolder> {

    TextView mRestaurantMatesName;

    private RequestManager glide;

    /**
     * Create constructor
     * @param glide
     */
    public RestaurantAdapter(FirestoreRecyclerOptions<User> options, RequestManager glide) {
        super(options);
        this.glide = glide;
    }

    @NonNull
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_restaurant_item, parent, false);
        mRestaurantMatesName = view.findViewById(R.id.restaurant_mates_name);
        return new RestaurantViewHolder(view);
    }


    protected void onBindViewHolder(@NonNull RestaurantViewHolder restaurantViewHolder, int position, @NonNull User model) {
        restaurantViewHolder.updateWithData(model, this.glide);
    }
}
