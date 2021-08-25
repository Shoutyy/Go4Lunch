package com.example.go4lunch.controllers.fragments.workmates;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.example.go4lunch.R;
import com.example.go4lunch.models.User;
import com.example.go4lunch.views.WorkmatesAdapter;

import io.reactivex.disposables.Disposable;

import com.example.go4lunch.databinding.FragmentWorkmatesBinding;

public class WorkmatesFragment extends Fragment {

    private WorkmatesViewModel workmatesViewModel;
    private FragmentWorkmatesBinding binding;
    private Disposable mDisposable;
    private RecyclerView mRecyclerViewMates;
    private WorkmatesAdapter workmatesAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionUsers = db.collection("users");

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        workmatesViewModel =
                new ViewModelProvider(this).get(WorkmatesViewModel.class);

        binding = FragmentWorkmatesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mRecyclerViewMates = binding.fragmentWorkmatesRV;

        setUpRecyclerView();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    /**
     * RecyclerView configuration
     * Configure RecyclerView, Adapter, LayoutManager & glue it
     */
    private void setUpRecyclerView() {
        Query query = collectionUsers.orderBy("placeId", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();
        this.workmatesAdapter = new WorkmatesAdapter(options, Glide.with(this));
        mRecyclerViewMates.setHasFixedSize(true);
        mRecyclerViewMates.setAdapter(workmatesAdapter);
        mRecyclerViewMates.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    @Override
    public void onStart() {
        super.onStart();
        workmatesAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        workmatesAdapter.stopListening();
    }

    /**
     * dispose subscription
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.disposeWhenDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    /**
     * dispose subscription
     */
    private void disposeWhenDestroy() {
        if (this.mDisposable != null && !this.mDisposable.isDisposed()) this.mDisposable.dispose();
    }
}