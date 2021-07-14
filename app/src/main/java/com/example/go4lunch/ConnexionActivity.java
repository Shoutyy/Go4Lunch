package com.example.go4lunch;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.example.go4lunch.databinding.ActivityConnexionBinding;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.List;
import androidx.annotation.Nullable;

public class ConnexionActivity extends BaseActivity<ActivityConnexionBinding> {

    private static final int RC_SIGN_IN = 123;

    ActivityConnexionBinding getViewBinding() {
        return ActivityConnexionBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // hide actionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // hide notificationBar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setupListeners();
    }

    private void setupListeners(){
        // connexion button
        binding.connexionButton.setOnClickListener(view -> startSignInActivity());
    }

    AuthMethodPickerLayout authenticationLayout = new AuthMethodPickerLayout
            .Builder(R.layout.activity_authentication)
            .setGoogleButtonId(R.id.googleButton)
            .setEmailButtonId(R.id.mailButton)
            .setTwitterButtonId(R.id.twitterButton)
            .setFacebookButtonId(R.id.facebookButton)
            // ...
            //.setTosAndPrivacyPolicyId(R.id.baz)
            .build();

    private void startSignInActivity() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.TwitterBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build());

        // Launch the activity
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAuthMethodPickerLayout(authenticationLayout)
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false, true)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    // Show Snack Bar with a message
    private void showSnackBar( String message){
        Snackbar.make(binding.connexionLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    // Method that handles response after SignIn Activity close
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            // SUCCESS
            if (resultCode == RESULT_OK) {
               // userManager.createUser();
                showSnackBar(getString(R.string.connection_succeed));
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            } else {
                // ERRORS
                if (response == null) {
                    showSnackBar(getString(R.string.error_authentication_canceled));
                } else if (response.getError()!= null) {
                    if(response.getError().getErrorCode() == ErrorCodes.NO_NETWORK){
                        showSnackBar(getString(R.string.error_no_internet));
                    } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                        showSnackBar(getString(R.string.error_unknown_error));
                    }
                }
            }
        }
    }
}


