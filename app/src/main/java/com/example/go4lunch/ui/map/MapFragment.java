package com.example.go4lunch.ui.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
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

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.NetworkAsyncTask;
import com.example.go4lunch.R;
import com.example.go4lunch.databinding.FragmentMapBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
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



public class MapFragment extends Fragment implements OnMapReadyCallback, NetworkAsyncTask.Listeners {

    private MapViewModel mapViewModel;
    private FragmentMapBinding binding;
    FusedLocationProviderClient client;
    SupportMapFragment supportMapFragment;
    GoogleMap mGoogleMap;
    private static final String TAG = "MyMapFragment";
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    double currentLat = 0, currentLong = 0;
    //String API_KEY = BuildConfig.MAPS_API_KEY;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mapViewModel =
                new ViewModelProvider(this).get(MapViewModel.class);

        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        View button = root.findViewById(R.id.ic_search);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startAutocompleteActivity();
                    }
                });

        String apiKey = getString(R.string.api_key);
        Places.initialize(getActivity().getApplicationContext(), apiKey);

        PlacesClient placesClient = Places.createClient(getContext());
/*
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setTypeFilter(TypeFilter.ESTABLISHMENT);
        autocompleteFragment.setCountries("FR");

        autocompleteFragment.setLocationBias(RectangularBounds.newInstance(
                new LatLng(48.8534, 2.3488),
                new LatLng(48.8534, 2.3488)
        ));




        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull @NotNull Place place) {
                Log.i(TAG ,"Place: " + place.getName() + ", " + place.getId());
            }

            @Override
            public void onError(@NonNull @NotNull Status status) {
                Log.i(TAG , "An error occurred: " + status);
            }
        });

*/
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        client = LocationServices.getFusedLocationProviderClient(getContext());
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        this.executeHttpRequest();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void getCurrentLocation() {
        @SuppressWarnings({"ResourceType"}) Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                supportMapFragment.getMapAsync(googleMap -> {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    currentLat = location.getLatitude();
                    currentLong = location.getLongitude();
                   // MarkerOptions options = new MarkerOptions().position(latLng).title("here");
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                   // googleMap.addMarker(options);
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

    public void startAutocompleteActivity() {
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY,
                Arrays.asList(Place.Field.ID, Place.Field.NAME))
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setLocationBias(RectangularBounds.newInstance(
                new LatLng(currentLat, currentLong),
                new LatLng(currentLat, currentLong)))
                .setCountry("FR")
                .build(getContext());
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    private void executeHttpRequest(){
        new NetworkAsyncTask(this).execute("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=48.8532,2.3430&radius=50&type=restaurant&key=AIzaSyAUVpNJRJVf23bYCsyhJPaU8-w2Dpoek8g");
        //new NetworkAsyncTask(this).execute("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + currentLat + "," + currentLong + "&radius=50&type=restaurant&key=" + myApiKey);
    }

    @Override
    public void onPreExecute() {
        this.updateUIWhenStartingHTTPRequest();
    }

    @Override
    public void doInBackground() {

    }

    @Override
    public void onPostExecute(String json) {
        this.updateUIWhenStopingHTTPRequest(json);
    }

    private void updateUIWhenStartingHTTPRequest() {

    }

    private void updateUIWhenStopingHTTPRequest(String response) {

    }

/*
    @Override
    public void OnActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getID());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {

            }
        }
    }

 */
}