package com.example.go4lunch.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.controllers.activities.RestaurantActivity;
import com.example.go4lunch.models.detail.PlaceDetail;
import com.example.go4lunch.models.User;
import com.example.go4lunch.models.detail.PlaceResult;
import com.example.go4lunch.models.nerby_search.ResultSearch;
import com.example.go4lunch.utils.ItemClickSupport;
import com.example.go4lunch.utils.PlaceStream;

import java.io.Serializable;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class WorkmatesViewHolder extends RecyclerView.ViewHolder {

    ImageView mWorkmatesPhoto;
    TextView mWorkmatesName;

    private Disposable mDisposable;
    private String restaurantName;
    private String idRestaurant;
    private String userName;
    private PlaceResult result;
    private PlaceDetail detail;


    public WorkmatesViewHolder(@NonNull View itemView) {
        super(itemView);
        mWorkmatesPhoto = itemView.findViewById(R.id.workmates_photo);
        mWorkmatesName = itemView.findViewById(R.id.workmates_name);

        //for retrieve restaurant sheet on click workmates

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), RestaurantActivity.class);
                if (detail.getResult() != null) {
                    intent.putExtra("placeId", detail.getResult().getPlaceId());
                    v.getContext().startActivity(intent);
                }
            });

    }

    @SuppressLint("SetTextI18n")
    public void updateWithDetails(User users, RequestManager glide) {
        userName = users.getUsername();
        idRestaurant = users.getPlaceId();

        Log.d("idRestaurantUser", "idRestaurantUsers" + " " + idRestaurant);
        executeHttpRequestWithRetrofit();

        if (users.getUrlPicture() != null && !users.getUrlPicture().isEmpty()) {
            glide.load(users.getUrlPicture()).apply(RequestOptions.circleCropTransform()).into(mWorkmatesPhoto);
        } else {
            mWorkmatesPhoto.setImageResource(R.drawable.no_picture);
        }
    }

    private void executeHttpRequestWithRetrofit() {
        this.mDisposable = PlaceStream.streamFetchDetails(idRestaurant)
                .subscribeWith(new DisposableObserver<PlaceDetail>() {

                    @Override
                    public void onNext(PlaceDetail placeDetail) {
                        detail = placeDetail;
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete() {

                        if (idRestaurant != null) {
                            restaurantName = detail.getResult().getName();
                            mWorkmatesName.setText(userName + " " + itemView.getContext().getString(R.string.eatWorkmates) + " " + restaurantName);
                            Log.d("OnCompleteRestauName", "restaurantName" + idRestaurant);
                        } else {
                            mWorkmatesName.setText(userName + " " + itemView.getContext().getString(R.string.no_decided));
                            Log.d("RestaurantName", "noRestaurant" + userName);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("onErrorWorkmates", Log.getStackTraceString(e));
                    }
                });
    }
}
