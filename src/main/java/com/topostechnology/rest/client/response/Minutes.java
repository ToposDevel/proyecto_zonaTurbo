package com.topostechnology.rest.client.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "totalAmt",
        "unusedAmt",
        "consumedAmt",
        "cuota",
        "effectiveDate",
        "expireDate",
        "count",
        "unlimited"
})
 public class Minutes {

    @JsonProperty("totalAmt")
    private Integer totalAmt;
    @JsonProperty("unusedAmt")
    private Integer unusedAmt;
    @JsonProperty("consumedAmt")
    private Integer consumedAmt;
    @JsonProperty("cuota")
    private String cuota;
    @JsonProperty("effectiveDate")
    private String effectiveDate;
    @JsonProperty("expireDate")
    private String expireDate;
    @JsonProperty("count")
    private Integer count;
    @JsonProperty("unlimited")
    private String unlimited;

    @JsonProperty("totalAmt")
    public Integer getTotalAmt() {
        return totalAmt;
    }

    @JsonProperty("totalAmt")
    public void setTotalAmt(Integer totalAmt) {
        this.totalAmt = totalAmt;
    }

    @JsonProperty("unusedAmt")
    public Integer getUnusedAmt() {
        return unusedAmt;
    }

    @JsonProperty("unusedAmt")
    public void setUnusedAmt(Integer unusedAmt) {
        this.unusedAmt = unusedAmt;
    }

    @JsonProperty("consumedAmt")
    public Integer getConsumedAmt() {
        return consumedAmt;
    }

    @JsonProperty("consumedAmt")
    public void setConsumedAmt(Integer consumedAmt) {
        this.consumedAmt = consumedAmt;
    }

    @JsonProperty("cuota")
    public String getCuota() {
        return cuota;
    }

    @JsonProperty("cuota")
    public void setCuota(String cuota) {
        this.cuota = cuota;
    }

    @JsonProperty("effectiveDate")
    public String getEffectiveDate() {
        return effectiveDate;
    }

    @JsonProperty("effectiveDate")
    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    @JsonProperty("expireDate")
    public String getExpireDate() {
        return expireDate;
    }

    @JsonProperty("expireDate")
    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    @JsonProperty("count")
    public Integer getCount() {
        return count;
    }

    @JsonProperty("count")
    public void setCount(Integer count) {
        this.count = count;
    }

    @JsonProperty("unlimited")
    public String getUnlimited() {
        return unlimited;
    }

    @JsonProperty("unlimited")
    public void setUnlimited(String unlimited) {
        this.unlimited = unlimited;
    }


}