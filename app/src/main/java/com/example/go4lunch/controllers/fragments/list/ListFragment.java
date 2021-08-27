package com.example.go4lunch.controllers.fragments.list;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
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
    private ListAdapter adapter;
    private RecyclerView mRecyclerView;
    FusedLocationProviderClient client;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    double currentLat = 0, currentLong = 0;
    String mPosition = currentLat + "," + currentLong;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        listViewModel =
                new ViewModelProvider(this).get(ListViewModel.class);

        binding = FragmentListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mRecyclerView = binding.fragmentListRV;

        this.configureRecyclerView();

        client = LocationServices.getFusedLocationProviderClient(getContext());

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        //executeHttpRequestWithRetrofit();
        return root;
    }

    private void configureRecyclerView() {
        this.resultSearches = new ArrayList<>();
        this.adapter = new ListAdapter(this.resultSearches, Glide.with(this), this.mPosition);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void executeHttpRequestWithRetrofit() {
        this.disposable = PlaceStream.streamFetchRestaurantList(mPosition, 300, "restaurant")
                .subscribeWith(new DisposableSingleObserver<List<ResultSearch>>() {

                    @Override
                    public void onSuccess(@NotNull List<ResultSearch> resultSearches) {
                        updateUI(resultSearches);
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

    private void updateUI(List<ResultSearch> resultSearches) {
        this.resultSearches.clear();
        this.resultSearches.addAll(resultSearches);
        Log.d("TestUI", resultSearches.toString());
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
                    ResultSearch resultSearch = adapter.getRestaurant(position);
                    Intent intent = new Intent(getActivity(), RestaurantActivity.class);
                    intent.putExtra("placeId", resultSearch.getPlaceId());
                    startActivity(intent);
                }));
    }
}