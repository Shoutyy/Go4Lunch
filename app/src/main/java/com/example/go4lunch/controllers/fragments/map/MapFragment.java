package com.example.go4lunch.controllers.fragments.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.databinding.FragmentMapBinding;
import com.example.go4lunch.models.detail.PlaceDetail;
import com.example.go4lunch.models.detail.PlaceResult;
import com.example.go4lunch.models.nerby_search.PlaceInfo;
import com.example.go4lunch.models.nerby_search.ResultSearch;
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
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;


import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    private MapViewModel mapViewModel;
    private FragmentMapBinding binding;
    FusedLocationProviderClient client;
    SupportMapFragment supportMapFragment;
    GoogleMap mGoogleMap;
    private static final String TAG = "MyMapFragment";
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    double currentLat = 0, currentLong = 0;
    String mPosition = currentLat + "," + currentLong;
    String API_KEY = BuildConfig.MAPS_API_KEY;
    private Disposable disposable;
    private Marker positionMarker;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);

        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        View button = root.findViewById(R.id.ic_search);

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
        this.disposable = PlaceStream.streamFetchRestaurantList(mPosition, 300, "restaurant")
                .subscribeWith(new DisposableSingleObserver<List<ResultSearch>>() {

                    @Override
                    public void onSuccess(@NotNull List<ResultSearch> resultSearches) {
                        positionMarker(resultSearches);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("TestDetail", Log.getStackTraceString(e));
                    }
                });
    }

    private void positionMarker(List<ResultSearch> resultSearches) {
        mGoogleMap.clear();
        for (ResultSearch search : resultSearches) {
           LatLng latLng = new LatLng(search.getGeometry().getLocation().getLat(),
                   search.getGeometry().getLocation().getLng()
            );
            positionMarker = mGoogleMap.addMarker(new MarkerOptions().position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.place_unbook_24))
                    .title(search.getName())
                    .snippet(search.getVicinity()));
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
}