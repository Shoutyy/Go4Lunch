package com.example.go4lunch.controllers.fragments.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.api.UserHelper;
import com.example.go4lunch.controllers.activities.RestaurantActivity;
import com.example.go4lunch.databinding.FragmentMapBinding;
import com.example.go4lunch.models.detail.PlaceDetail;
import com.example.go4lunch.utils.PlaceStream;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    private MapViewModel mapViewModel;
    private FragmentMapBinding binding;
    FusedLocationProviderClient client;
    SupportMapFragment supportMapFragment;
    GoogleMap mGoogleMap;
    private static final String TAG = "MyMapFragment";
    double currentLat = 0, currentLong = 0;
    String mPosition = currentLat + "," + currentLong;
    String API_KEY = BuildConfig.MAPS_API_KEY;
    private Disposable disposable;
    private Marker positionMarker;
    public List<PlaceDetail> placeDetails;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);

        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //for SearchView
        setHasOptionsMenu(true);

        Places.initialize(getActivity().getApplicationContext(), API_KEY);

        PlacesClient placesClient = Places.createClient(getContext());

        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        client = LocationServices.getFusedLocationProviderClient(getContext());

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle(R.string.title_bar);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        getCurrentLocation();
    }

    private void getCurrentLocation() {
        @SuppressLint("MissingPermission") @SuppressWarnings({"ResourceType"}) Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                supportMapFragment.getMapAsync(googleMap -> {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    currentLat = location.getLatitude();
                    currentLong = location.getLongitude();
                    mPosition =  currentLat + "," + currentLong;
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                    this.executeHttpRequestWithRetrofit();
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMyLocationEnabled(true);
    }

    private void executeHttpRequestWithRetrofit() {
        this.disposable = PlaceStream.streamFetchRestaurantDetails(mPosition, 300, "restaurant")
                .subscribeWith(new DisposableSingleObserver<List<PlaceDetail>>() {

                    @Override
                    public void onSuccess(@NotNull List<PlaceDetail> placeDetails) {
                        positionMarker(placeDetails);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("TestDetail", Log.getStackTraceString(e));
                    }
                });
        if (mGoogleMap != null) {
            mGoogleMap.setOnInfoWindowClickListener(marker -> {
                //for retrieve result
                Intent intent = new Intent(getContext(), RestaurantActivity.class);
                intent.putExtra("placeId", marker.getTag().toString());
                startActivity(intent);
            });
        }
    }

    private void positionMarker(List<PlaceDetail> placeDetails) {
        mGoogleMap.clear();
        for (PlaceDetail detail : placeDetails) {
            LatLng latLng = new LatLng(detail.getResult().getGeometry().getLocation().getLat(),
                    detail.getResult().getGeometry().getLocation().getLng()
            );
            UserHelper.getUsersCollection()
                    .whereEqualTo("placeId", detail.getResult().getPlaceId())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                                Log.d("numberWorkmates", documentSnapshot.getId() + " " + documentSnapshot.getData());
                            }
                            int numberWorkmates = Objects.requireNonNull(task.getResult()).size();
                            if (numberWorkmates != 0) {
                                positionMarker = mGoogleMap.addMarker(new MarkerOptions().position(latLng)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.place_booked_24))
                                        .title(detail.getResult().getName())
                                        .snippet(detail.getResult().getVicinity()));
                            } else {
                                positionMarker = mGoogleMap.addMarker(new MarkerOptions().position(latLng)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.place_unbook_24))
                                        .title(detail.getResult().getName())
                                        .snippet(detail.getResult().getVicinity()));
                            }
                            if (positionMarker != null) {
                                positionMarker.setTag(detail.getResult().getPlaceId());
                            }
                        } else {
                            Log.e("numberMatesError", "Error getting documents: ", task.getException());
                        }
                    });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.disposeWhenDestroy();
    }

    private void disposeWhenDestroy(){
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.toolbar_menu, menu);
        MenuItem item = menu.findItem(R.id.actionSearch);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    executeHttpRequestWithRetrofit();
                }
                executeHttpRequestWithRetrofitAutocomplete(newText);
                return true;
            }
        });
    }

    private void executeHttpRequestWithRetrofitAutocomplete(String input) {

        this.disposable = PlaceStream.streamFetchAutoCompleteInfos(input, 2000, mPosition)
                .subscribeWith(new DisposableSingleObserver<List<PlaceDetail>>() {

                    @Override
                    public void onSuccess(List<PlaceDetail> placeDetails) {
                        positionMarker(placeDetails);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("TestAutocomplete", Log.getStackTraceString(e));
                    }
                });
        if (mGoogleMap != null) {
            mGoogleMap.setOnInfoWindowClickListener(marker -> {
                //for retrieve result
                Intent intent = new Intent(getContext(), RestaurantActivity.class);
                intent.putExtra("placeId", marker.getTag().toString());
                startActivity(intent);
            });
        }
    }
}