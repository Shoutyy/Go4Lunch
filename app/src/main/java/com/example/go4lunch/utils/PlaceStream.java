package com.example.go4lunch.utils;


import com.example.go4lunch.models.autocomplete.AutoCompleteResult;
import com.example.go4lunch.models.autocomplete.Prediction;
import com.example.go4lunch.models.detail.PlaceDetail;
import com.example.go4lunch.models.detail.PlaceResult;
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
    public static Observable<AutoCompleteResult> streamFetchAutoComplete(String input, int radius, String location) {
        return mapPlacesService.getAutoComplete(input, radius, location)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    /*
    public static  Observable<List<PlaceResult>> streamFetchAutoCompleteInfo(String input, int radius, String location){
        return mapPlacesService.getAutoComplete(input, radius, location)
                .flatMapIterable(AutoCompleteResult::getPredictions)
                .flatMap(info -> mapPlacesService.getDetails(info.getPlaceId()))
                .map(PlaceDetail::getResult)
                .toList()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

     */
    public static Single<List<PlaceDetail>> streamFetchAutoCompleteInfos(String input, int radius, String location) {
        return streamFetchAutoComplete(input, radius, location)
                .flatMapIterable(new Function<AutoCompleteResult, List<Prediction>>() {
                    List<Prediction> food = new ArrayList<>();

                    @Override
                    public List<Prediction> apply(AutoCompleteResult autoCompleteResult) throws Exception {

                        for (Prediction prediction : autoCompleteResult.getPredictions()) {
                            if (prediction.getTypes().contains("food")) {

                                food.add(prediction);
                            }
                        }
                        return food;
                    }
                })
                .flatMap(new Function<Prediction, ObservableSource<PlaceDetail>>() {
                    @Override
                    public ObservableSource<PlaceDetail> apply(Prediction prediction) throws Exception {
                        return streamFetchDetails(prediction.getPlaceId());
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
