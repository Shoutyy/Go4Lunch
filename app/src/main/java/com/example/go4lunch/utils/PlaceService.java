package com.example.go4lunch.utils;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.models.autocomplete.AutoCompleteResult;
import com.example.go4lunch.models.detail.PlaceDetail;
import com.example.go4lunch.models.nerby_search.PlaceInfo;
import com.example.go4lunch.models.nerby_search.ResultSearch;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlaceService {
    String API_KEY = BuildConfig.MAPS_API_KEY;

    //GoogleMap API Request
    @GET("maps/api/place/nearbysearch/json?key="+API_KEY)
    Observable<PlaceInfo> getRestaurants(@Query("location") String location, @Query("radius") int radius, @Query("type") String type);

    //PlaceDetails API Request
    @GET("maps/api/place/details/json?key="+API_KEY)
    Observable<PlaceDetail> getDetails(@Query("place_id") String placeId);

    //Autocomplete API Request
    @GET("maps/api/place/autocomplete/json?strictbounds&types=establishment&key="+API_KEY)
    Observable<AutoCompleteResult> getAutoComplete(@Query("input") String input, @Query("radius") int radius, @Query("location") String location);

    public static final Retrofit retrofit = new Retrofit.Builder()
            //define root URL
            .baseUrl("https://maps.googleapis.com/")
            //serialize Gson
            .addConverterFactory(GsonConverterFactory.create())
            //For RxJava
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();
}
