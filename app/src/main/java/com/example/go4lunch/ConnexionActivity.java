package com.example.go4lunch;

import android.os.Bundle;
import android.view.WindowManager;

import com.example.go4lunch.databinding.ActivityConnexionBinding;
import com.firebase.ui.auth.AuthUI;

import java.util.Collections;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class ConnexionActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private ActivityConnexionBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // hide actionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // hide notificationBar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        binding = ActivityConnexionBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_connexion);
        setupListeners();
    }

    private void setupListeners(){
        // mail button
        binding.mailButton.setOnClickListener(view -> {
            startSignInActivity();
        });
    }

    private void startSignInActivity() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers =
                Collections.singletonList(new AuthUI.IdpConfig.EmailBuilder().build());

        // Launch the activity
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.white_bowl_lowsize)
                        .build(),
                RC_SIGN_IN);
    }
}


