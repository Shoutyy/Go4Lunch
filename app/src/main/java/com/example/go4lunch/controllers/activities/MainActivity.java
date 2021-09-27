package com.example.go4lunch.controllers.activities;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.api.UserHelper;
import com.example.go4lunch.models.User;
import com.example.go4lunch.models.detail.PlaceDetail;
import com.example.go4lunch.utils.FirebaseUtils;
import com.example.go4lunch.utils.PlaceStream;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

import com.example.go4lunch.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import static com.example.go4lunch.utils.FirebaseUtils.getCurrentUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int SIGN_OUT_TASK = 100;
    private ActivityMainBinding binding;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView mNavigationView;
    private Disposable mDisposable;
    private PlaceDetail detail;
    private String idRestaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

        toolbar = findViewById(R.id.main_page_toolbar);
        mNavigationView = findViewById(R.id.main_page_nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        configureToolbar();
        configureDrawerLayout();
        updateUINavHeader();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.title_bar);
        }
    }

    private void configureToolbar() {
        setSupportActionBar(toolbar);
    }

    private void configureDrawerLayout() {
        this.drawerLayout = findViewById(R.id.main_page_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_drawer_lunch) {
            if (FirebaseUtils.getCurrentUser() != null) {
                UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    if (Objects.requireNonNull(user).getPlaceId() != null) {
                        userRestaurant(user);
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.no_restaurant_choose), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else if (id == R.id.menu_drawer_settings) {
            Intent settingIntent = new Intent(this, SettingActivity.class);
            startActivity(settingIntent);
        } else if (id == R.id.menu_drawer_Logout) {
            signOutFromUserFirebase();
            Toast.makeText(getApplicationContext(), getString(R.string.deconnected), Toast.LENGTH_SHORT).show();
        }
        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    private void userRestaurant(User users) {
        idRestaurant = users.getPlaceId();
        executeHttpRequestWithRetrofit();
    }

    private void executeHttpRequestWithRetrofit() {
        this.mDisposable = PlaceStream.streamFetchDetails(idRestaurant)
                .subscribeWith(new DisposableObserver<PlaceDetail>() {

                    @Override
                    public void onNext(PlaceDetail placeDetail) {
                        detail = placeDetail;
                        startForLunch();
                    }

                    @Override
                    public void onComplete() {
                        if (idRestaurant != null) {
                            Log.d("your lunch request", "your lunch" + detail.getResult());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("onErrorYourLunch", Log.getStackTraceString(e));
                    }
                });
    }

    private void signOutFromUserFirebase() {
        if (FirebaseUtils.getCurrentUser() != null) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnSuccessListener(this, this.updateUIAfterRestRequestsCompleted());
        }
    }

    private OnSuccessListener<Void> updateUIAfterRestRequestsCompleted() {
        return aVoid -> {
            finish();
        };
    }

    public void startForLunch() {
        Intent intent = new Intent(this, RestaurantActivity.class);
        intent.putExtra("placeId", detail.getResult().getPlaceId());
        this.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void updateUINavHeader() {
        if (FirebaseUtils.getCurrentUser() != null) {
            View headerView = mNavigationView.getHeaderView(0); //For return layout
            ImageView mPhotoHeader = headerView.findViewById(R.id.photo_header);
            TextView mNameHeader = headerView.findViewById(R.id.name_header);
            TextView mMailHeader = headerView.findViewById(R.id.mail_header);
            // get photo in Firebase
            if (FirebaseUtils.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(FirebaseUtils.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(mPhotoHeader);
            } else {
                mPhotoHeader.setImageResource(R.drawable.no_picture);
            }
            //Get email
            String email = TextUtils.isEmpty(FirebaseUtils.getCurrentUser().getEmail()) ?
                    (getString(R.string.info_no_email_found)) : FirebaseUtils.getCurrentUser().getEmail();
            //Get Name
            String name = TextUtils.isEmpty(FirebaseUtils.getCurrentUser().getDisplayName()) ?
                    (getString(R.string.info_no_username_found)) : FirebaseUtils.getCurrentUser().getDisplayName();
            //Update With data
            mNameHeader.setText(name);
            mMailHeader.setText(email);
        }
    }
}