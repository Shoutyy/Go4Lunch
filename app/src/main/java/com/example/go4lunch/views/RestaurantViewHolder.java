package com.example.go4lunch.views;

import android.annotation.SuppressLint;
import android.view.View;

import com.bumptech.glide.RequestManager;
import com.firebase.ui.auth.data.model.User;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RestaurantViewHolder extends RecyclerView.ViewHolder {

    public RestaurantViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);
    }


    @SuppressLint("SetTextI18n")
    public void updateWithData(User users, RequestManager glide) {

    }

    private void executeHttpRequestWithRetrofit() {

    }
}
