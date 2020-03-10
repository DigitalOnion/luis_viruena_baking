package com.outerspace.baking.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Ingredient {
    @SerializedName("quantity")
    @Expose
    public float quantity;
    @SerializedName("measure")
    @Expose
    public String measure;
    @SerializedName("ingredient")
    @Expose
    public String ingredient;
}