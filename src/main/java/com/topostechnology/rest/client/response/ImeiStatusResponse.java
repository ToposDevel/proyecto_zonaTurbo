package com.topostechnology.rest.client.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "imei",
        "deviceFeatures"
})
public class ImeiStatusResponse {

    @JsonProperty("imei")
    private ImeiDevice imei;
    @JsonProperty("deviceFeatures")
    private DeviceFeatures deviceFeatures;

    @JsonProperty("imei")
    public ImeiDevice getImei() {
        return imei;
    }

    @JsonProperty("imei")
    public void setImei(ImeiDevice imei) {
        this.imei = imei;
    }

    @JsonProperty("deviceFeatures")
    public DeviceFeatures getDeviceFeatures() {
        return deviceFeatures;
    }

    @JsonProperty("deviceFeatures")
    public void setDeviceFeatures(DeviceFeatures deviceFeatures) {
        this.deviceFeatures = deviceFeatures;
    }

}