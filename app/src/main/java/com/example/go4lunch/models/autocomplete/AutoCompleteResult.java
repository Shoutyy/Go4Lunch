package com.example.go4lunch.models.autocomplete;

import java.util.List;
import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class AutoCompleteResult {

    @SerializedName("predictions")
    private List<Prediction> mPredictions;
    @SerializedName("status")
    private String mStatus;

    public List<Prediction> getPredictions() {
        return mPredictions;
    }

    public void setPredictions(List<Prediction> predictions) {
        mPredictions = predictions;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

}
