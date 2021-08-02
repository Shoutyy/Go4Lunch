package com.example.go4lunch.utils;

import com.example.go4lunch.models.detail.PlaceDetail;
import com.example.go4lunch.models.nerby_search.PlaceInfo;
import com.example.go4lunch.models.nerby_search.ResultSearch;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class PlaceStream {
    private static PlaceService mapPlacesService = PlaceService.retrofit.create(PlaceService.class);

    public static Observable<PlaceInfo> streamFetchRestaurants(String location, int radius, String type) {
        return mapPlacesService.getRestaurants(location, radius, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public static Observable<PlaceDetail> streamFetchDetails(String placeId) {
        return mapPlacesService.getDetails(placeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public static  Single<List<ResultSearch>> streamFetchRestaurantDetails(String location, int radius, String type){
        return streamFetchRestaurants(location,radius,type)
                .flatMapIterable(PlaceInfo::getResults)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

}
