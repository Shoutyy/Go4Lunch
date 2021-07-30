package com.example.go4lunch.models.detail;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlaceDetail {

    @SerializedName("html_attributions")
    @Expose
    private List<Object> htmlAttributions = null;
    @SerializedName("placeResult")
    @Expose
    private PlaceResult placeResult;
    @SerializedName("status")
    @Expose
    private String status;

    public List<Object> getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(List<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public PlaceResult getResult() {
        return placeResult;
    }

    public void setResult(PlaceResult placeResult) {
        this.placeResult = placeResult;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
