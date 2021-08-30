package com.example.go4lunch.models.autocomplete;

import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class Term {

    @SerializedName("offset")
    private Long mOffset;
    @SerializedName("value")
    private String mValue;

    public Long getOffset() {
        return mOffset;
    }

    public void setOffset(Long offset) {
        mOffset = offset;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue = value;
    }
}

