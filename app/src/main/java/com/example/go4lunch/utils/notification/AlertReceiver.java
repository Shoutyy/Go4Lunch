package com.example.go4lunch.utils.notification;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.go4lunch.api.UserHelper;
import com.example.go4lunch.models.User;
import com.example.go4lunch.models.detail.PlaceDetail;
import com.example.go4lunch.utils.FirebaseUtils;
import com.example.go4lunch.utils.PlaceStream;
import com.example.go4lunch.R;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

import androidx.core.app.NotificationCompat;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class AlertReceiver extends BroadcastReceiver {
    private String userIdNotif;
    private PlaceDetail detail;
    private String restaurantNotifName;
    private Disposable mDisposable;
    private String restaurantNotifAddress;

    private String nameNotif;
    private String notifMessage;
    private Context context;


    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        // request for placeId and time
        UserHelper.getUser(Objects.requireNonNull(FirebaseUtils.getCurrentUser()).getUid()).addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            if (user != null) {
                if (!user.getPlaceId().isEmpty()) {

                    userIdNotif = user.getPlaceId();
                    executeHttpRequestWithRetrofit();
                    Log.d("TestNotifId", userIdNotif);
                }
            }
        });
    }

    private void executeHttpRequestWithRetrofit() {
        this.mDisposable = PlaceStream.streamFetchDetails(userIdNotif)
                .subscribeWith(new DisposableObserver<PlaceDetail>() {

                    @Override
                    public void onNext(PlaceDetail placeDetail) {
                        detail = placeDetail;
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete() {
                        if (userIdNotif != null) {
                            restaurantNotifName = detail.getResult().getName();
                            restaurantNotifAddress = detail.getResult().getVicinity();
                            workmatesNotif(userIdNotif);

                            Log.d("RestaurantNameNotif", restaurantNotifName + " " + restaurantNotifAddress + " " + nameNotif + " " + notifMessage);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("onErrorRestaurantNotif", Log.getStackTraceString(e));
                    }
                });
    }

    //For retrieve workmates who chose this restaurant and the time
    private void workmatesNotif(String userIdNotif) {
        UserHelper.getUsersCollection()
                .whereEqualTo("placeId", userIdNotif)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                            Log.d("workmatesNotif", documentSnapshot.getId() + " " + documentSnapshot.getData());
                            nameNotif = String.valueOf(documentSnapshot.get("username"));
                            Log.d("nameNotif", Objects.requireNonNull(nameNotif));

                            if (nameNotif != null) {
                                notifMessage = (context.getString(R.string.lunch_at) + " " + restaurantNotifName + " " +
                                        restaurantNotifAddress + " " + context.getString(R.string.with) + " " + nameNotif);
                            } else {
                                notifMessage = (context.getString(R.string.lunch_at) + " " + restaurantNotifName + " " +
                                        restaurantNotifAddress + " " + context.getString(R.string.alone));
                            }
                            NotificationHelper notificationHelper = new NotificationHelper(context);
                            NotificationCompat.Builder nb = notificationHelper.getChannelNotification(notifMessage);
                            notificationHelper.getManager().notify(1, nb.build());
                        }

                    } else {
                        Log.e("numberMatesError", "Error getting documents: ", task.getException());
                    }
                });
    }
}
