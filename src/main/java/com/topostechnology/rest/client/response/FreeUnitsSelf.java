package com.topostechnology.rest.client.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "primarySpeed",
        "count",
        "totalAmt",
        "unusedAmt",
        "consumedAmt",
        "cuota",
        "effectiveDate",
        "expireDate",
        "type",
        "downspeed",
        "permiteDownspeed",
        "unlimited",
        "remainder",
        "renderAmount"
})
public class FreeUnitsSelf {

    @JsonProperty("primarySpeed")
    private String primarySpeed;
    @JsonProperty("count")
    private Integer count;
    @JsonProperty("totalAmt")
    private Integer totalAmt;
    @JsonProperty("unusedAmt")
    private Double unusedAmt;
    @JsonProperty("consumedAmt")
    private Double consumedAmt;
    @JsonProperty("cuota")
    private String cuota;
    @JsonProperty("effectiveDate")
    private String effectiveDate;
    @JsonProperty("expireDate")
    private String expireDate;
    @JsonProperty("type")
    private String type;
    @JsonProperty("downspeed")
    private String downspeed;
    @JsonProperty("permiteDownspeed")
    private String permiteDownspeed;
    @JsonProperty("unlimited")
    private String unlimited;
    @JsonProperty("remainder")
    private Integer remainder;
    @JsonProperty("renderAmount")
    private Integer renderAmount;


    @JsonProperty("primarySpeed")
    public String getPrimarySpeed() {
        return primarySpeed;
    }

    @JsonProperty("primarySpeed")
    public void setPrimarySpeed(String primarySpeed) {
        this.primarySpeed = primarySpeed;
    }

    @JsonProperty("count")
    public Integer getCount() {
        return count;
    }

    @JsonProperty("count")
    public void setCount(Integer count) {
        this.count = count;
    }

    @JsonProperty("totalAmt")
    public Integer getTotalAmt() {
        return totalAmt;
    }

    @JsonProperty("totalAmt")
    public void setTotalAmt(Integer totalAmt) {
        this.totalAmt = totalAmt;
    }

    @JsonProperty("unusedAmt")
    public Double getUnusedAmt() {
        return unusedAmt;
    }

    @JsonProperty("unusedAmt")
    public void setUnusedAmt(Double unusedAmt) {
        this.unusedAmt = unusedAmt;
    }

    @JsonProperty("consumedAmt")
    public Double getConsumedAmt() {
        return consumedAmt;
    }

    @JsonProperty("consumedAmt")
    public void setConsumedAmt(Double consumedAmt) {
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

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("downspeed")
    public String getDownspeed() {
        return downspeed;
    }

    @JsonProperty("downspeed")
    public void setDownspeed(String downspeed) {
        this.downspeed = downspeed;
    }

    @JsonProperty("permiteDownspeed")
    public String getPermiteDownspeed() {
        return permiteDownspeed;
    }

    @JsonProperty("permiteDownspeed")
    public void setPermiteDownspeed(String permiteDownspeed) {
        this.permiteDownspeed = permiteDownspeed;
    }

    @JsonProperty("unlimited")
    public String getUnlimited() {
        return unlimited;
    }

    @JsonProperty("unlimited")
    public void setUnlimited(String unlimited) {
        this.unlimited = unlimited;
    }

    @JsonProperty("remainder")
    public Integer getRemainder() {
        return remainder;
    }

    @JsonProperty("remainder")
    public void setRemainder(Integer remainder) {
        this.remainder = remainder;
    }

    @JsonProperty("renderAmount")
    public Integer getRenderAmount() {
        return renderAmount;
    }

    @JsonProperty("renderAmount")
    public void setRenderAmount(Integer renderAmount) {
        this.renderAmount = renderAmount;
    }


}