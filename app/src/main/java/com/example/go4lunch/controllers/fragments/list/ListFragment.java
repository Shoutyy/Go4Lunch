package com.example.go4lunch.controllers.fragments.list;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;

import com.example.go4lunch.controllers.activities.RestaurantActivity;
import com.example.go4lunch.databinding.FragmentListBinding;
import com.example.go4lunch.models.detail.PlaceDetail;
import com.example.go4lunch.models.nerby_search.ResultSearch;
import com.example.go4lunch.utils.ItemClickSupport;
import com.example.go4lunch.utils.PlaceStream;
import com.example.go4lunch.views.ListAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {

    private ListViewModel listViewModel;
    private FragmentListBinding binding;
    private Disposable disposable;
    private List<ResultSearch> resultSearches;
    private List<PlaceDetail> placeDetails;
    private ListAdapter adapter;
    private ListAdapter adapterA;
    private RecyclerView mRecyclerView;
    FusedLocationProviderClient client;
    double currentLat = 0, currentLong = 0;
    String mPosition = currentLat + "," + currentLong;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        listViewModel =
                new ViewModelProvider(this).get(ListViewModel.class);

        binding = FragmentListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setHasOptionsMenu(true);

        mRecyclerView = binding.fragmentListRV;

        this.configureRecyclerView();

        client = LocationServices.getFusedLocationProviderClient(getContext());

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        return root;
    }

    private void configureRecyclerView() {
        this.placeDetails = new ArrayList<>();
        this.adapter = new ListAdapter(this.placeDetails, Glide.with(this), this.mPosition);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void executeHttpRequestWithRetrofit() {
        this.disposable = PlaceStream.streamFetchRestaurantDetails(mPosition, 300, "restaurant")
                .subscribeWith(new DisposableSingleObserver<List<PlaceDetail>>() {

                    @Override
                    public void onSuccess(@NotNull List<PlaceDetail> placeDetails) {
                        updateUI(placeDetails);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("TestDetail", Log.getStackTraceString(e));
                    }
                });
    }

    private void getCurrentLocation() {
        @SuppressLint("MissingPermission") @SuppressWarnings({"ResourceType"}) Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                currentLat = location.getLatitude();
                currentLong = location.getLongitude();
                mPosition =  currentLat + "," + currentLong;
                this.executeHttpRequestWithRetrofit();
                this.configureOnClickRecyclerView();
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

    private void updateUI(List<PlaceDetail> placeDetails) {
        this.placeDetails.clear();
        this.placeDetails.addAll(placeDetails);
       // Log.d("TestUI", resultSearches.toString());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.disposeWhenDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void disposeWhenDestroy() {
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

    private void configureOnClickRecyclerView() {
        ItemClickSupport.addTo(mRecyclerView, R.layout.fragment_list_item)
                .setOnItemClickListener(((recyclerView, position, v) -> {
                    PlaceDetail placeDetail = adapter.getRestaurant(position);
                    Intent intent = new Intent(getActivity(), RestaurantActivity.class);
                    intent.putExtra("placeId", placeDetail.getResult().getPlaceId());
                    startActivity(intent);
                }));
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
                        updateUI(placeDetails);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("TestAutocomplete", Log.getStackTraceString(e));
                    }
                });
    }
}