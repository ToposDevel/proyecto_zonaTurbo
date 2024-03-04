package com.topostechnology.rest.client.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "volteCapable",
        "band28",
        "model",
        "brand"
})
public class DeviceFeatures {

    @JsonProperty("volteCapable")
    private String volteCapable;
    @JsonProperty("band28")
    private String band28;
    @JsonProperty("model")
    private String model;
    @JsonProperty("brand")
    private String brand;

    @JsonProperty("volteCapable")
    public String getVolteCapable() {
        return volteCapable;
    }

    @JsonProperty("volteCapable")
    public void setVolteCapable(String volteCapable) {
        this.volteCapable = volteCapable;
    }

    @JsonProperty("band28")
    public String getBand28() {
        return band28;
    }

    @JsonProperty("band28")
    public void setBand28(String band28) {
        this.band28 = band28;
    }

    @JsonProperty("model")
    public String getModel() {
        return model;
    }

    @JsonProperty("model")
    public void setModel(String model) {
        this.model = model;
    }

    @JsonProperty("brand")
    public String getBrand() {
        return brand;
    }

    @JsonProperty("brand")
    public void setBrand(String brand) {
        this.brand = brand;
    }

}