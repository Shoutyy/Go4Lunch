package com.example.go4lunch.utils;


import com.example.go4lunch.models.autocomplete.AutoCompleteResult;
import com.example.go4lunch.models.autocomplete.Prediction;
import com.example.go4lunch.models.detail.PlaceDetail;
import com.example.go4lunch.models.nerby_search.PlaceInfo;
import com.example.go4lunch.models.nerby_search.ResultSearch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
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

    public static  Single<List<ResultSearch>> streamFetchRestaurantList(String location, int radius, String type){
        return streamFetchRestaurants(location,radius,type)
                .flatMapIterable(PlaceInfo::getResults)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    //For autocomplete
    public static Observable<AutoCompleteResult> streamFetchAutocomplete(String input, int radius, String location) {
        return mapPlacesService.getAutocomplete(input, radius, location)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    //For autocomplete 2 chained request
    public static Single<List<PlaceDetail>> streamFetchAutocompleteInfos(String input, int radius, String location) {
        return streamFetchAutocomplete(input, radius, location)
                .flatMapIterable(new Function<AutoCompleteResult, List<Prediction>>() {
                    List<Prediction> food = new ArrayList<>();

                    @Override
                    public List<Prediction> apply(AutoCompleteResult autocompleteResult) throws Exception {

                        for (Prediction prediction : autocompleteResult.getPredictions()) {
                            if (prediction.getTypes().contains("food")) {

                                food.add(prediction);
                            }
                        }
                        return food;
                    }
                })
                .flatMap((Function<Prediction, ObservableSource<PlaceDetail>>) prediction -> streamFetchDetails(prediction.getPlaceId()))
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
