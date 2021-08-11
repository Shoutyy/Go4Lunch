package com.example.go4lunch.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.go4lunch.R;
import com.bumptech.glide.RequestManager;

import com.example.go4lunch.models.nerby_search.ResultSearch;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ListAdapter extends RecyclerView.Adapter<ListViewHolder> {

    //Declarations
    private String mPosition;
    private RequestManager glide;
    private List<ResultSearch> resultSearches;

    public void setPosition(String position) {
        mPosition = position;
    }

    /**
     * Constructor
     *
     * @param resultSearches
     * @param glide
     * @param mPosition
     */
    public ListAdapter(List<ResultSearch> resultSearches, RequestManager glide, String mPosition) {
        this.resultSearches = resultSearches;
        this.glide = glide;
        this.mPosition = mPosition;
    }

    /**
     * Create viewHolder
     *
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_list_item, parent, false);


        return new ListViewHolder(view);
    }

    /**
     * Update viewHolder with resultSearches
     *
     * @param viewHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ListViewHolder viewHolder, int position) {
        viewHolder.updateWithData(this.resultSearches.get(position), this.glide, this.mPosition);
    }

    /**
     * return the total count of items in the list
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return this.resultSearches.size();
    }
}
