package com.example.go4lunch.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.controllers.activities.RestaurantActivity;
import com.example.go4lunch.models.detail.PlaceDetail;
import com.example.go4lunch.models.User;
import com.example.go4lunch.R;
import com.example.go4lunch.utils.PlaceStream;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class RestaurantViewHolder extends RecyclerView.ViewHolder {

    ImageView mRestaurantMatesPhoto;
    TextView mRestaurantMatesName;

    private PlaceDetail detail;
    private Disposable mDisposable;
    private String restaurantName;
    private String idRestaurant;
    private String userName;

    public RestaurantViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);
        mRestaurantMatesPhoto = itemView.findViewById(R.id.restaurant_mates_photo);
        mRestaurantMatesName = itemView.findViewById(R.id.restaurant_mates_name);

        itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), RestaurantActivity.class);
            if (detail.getResult() != null) {
                intent.putExtra("placeId", detail.getResult().getPlaceId());
                v.getContext().startActivity(intent);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void updateWithData(User users, RequestManager glide) {

        userName = users.getUsername();
        idRestaurant = users.getPlaceId();
        executeHttpRequestWithRetrofit();

        if (users.getUrlPicture() != null && !users.getUrlPicture().isEmpty()) {
            glide.load(users.getUrlPicture()).apply(RequestOptions.circleCropTransform()).into(mRestaurantMatesPhoto);
        } else {
            mRestaurantMatesPhoto.setImageResource(R.drawable.no_picture);
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

                            mRestaurantMatesName.setText(userName + " " + itemView.getContext().getString(R.string.eat_at) + " " + restaurantName);
                            Log.d("OnCompleteRestauName", "restaurantName" + idRestaurant);
                        } else {
                            mRestaurantMatesName.setText(userName + " " + itemView.getContext().getString(R.string.not_decided));
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
