package com.example.go4lunch.utils;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.go4lunch.R;

public class FirebaseUtils {

    private static Context context;

    @Nullable
    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static Boolean isCurrentUserLogged () {
        return (getCurrentUser() != null);
    }

    /**
     * Error Handler
     * @return
     */

    public static OnFailureListener onFailureListener(){
        return e -> Toast.makeText(context, R.string.unknown_error, Toast.LENGTH_LONG).show();
    }

}
