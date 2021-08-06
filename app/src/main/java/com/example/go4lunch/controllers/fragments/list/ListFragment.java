package com.example.go4lunch.controllers.fragments.list;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;

import com.example.go4lunch.databinding.FragmentListBinding;
import com.example.go4lunch.models.nerby_search.ResultSearch;
import com.example.go4lunch.utils.PlaceStream;
import com.example.go4lunch.views.ListAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {

    private ListViewModel listViewModel;
    private FragmentListBinding binding;
    private Disposable disposable;
    private List<ResultSearch> resultSearches;
    private ListAdapter adapter;
    private String mPosition;
    private RecyclerView recyclerView;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        listViewModel =
                new ViewModelProvider(this).get(ListViewModel.class);

        binding = FragmentListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        RecyclerView recyclerView = root.findViewById(R.id.fragment_list_RV);

        this.resultSearches = new ArrayList<>();
        //create adapter passing the list of restaurants
        this.adapter = new ListAdapter(this.resultSearches, Glide.with(this), this.mPosition);
        //Attach the adapter to the recyclerview to items
        recyclerView.setAdapter(adapter);
        //Set layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

       // this.configureRecyclerView();

        return root;
    }

    /*
    private void configureRecyclerView() {
        //reset List
        this.resultSearches = new ArrayList<>();
        //create adapter passing the list of restaurants
        this.adapter = new ListAdapter(this.resultSearches, Glide.with(this), this.mPosition);
        //Attach the adapter to the recyclerview to items
        recyclerView.setAdapter(adapter);
        //Set layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

     */

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

    private void disposeWhenDestroy() {
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

}