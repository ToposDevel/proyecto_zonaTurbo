package com.topostechnology.rest.client.response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "msisdnStatus",
         "satatus",
        "descriptionStatus",
         "statusCode",
        "hasPromo",
        "lastTicketAmount",
        "transationDate",
        "freeUnits",
        "sms",
        "minutes",
        "freeUnitDetails"
})
public class SelfConsumption {

    @JsonProperty("msisdnStatus")
    private String msisdnStatus;
    @JsonProperty("descriptionStatus")
    private String descriptionStatus;
    @JsonProperty("hasPromo")
    private String hasPromo;
    @JsonProperty("lastTicketAmount")
    private String lastTicketAmount;
    @JsonProperty("freeUnits")
    private FreeUnitsSelf freeUnitsSelf;
    @JsonProperty("sms")
    private Sms sms;
    @JsonProperty("minutes")
    private Minutes minutes;
    @JsonProperty("status")
    private String status;
    @JsonProperty("statusCode")
    private String statusCode;
    @JsonProperty("transationDate")
    private String transationDate;
    @JsonProperty("freeUnitDetails")
    private List<FreeUnitDetail> freeUnitDetails = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getTransationDate() {
        return transationDate;
    }

    public void setTransationDate(String transationDate) {
        this.transationDate = transationDate;
    }

    @JsonProperty("msisdnStatus")
    public String getMsisdnStatus() {
        return msisdnStatus;
    }

    @JsonProperty("msisdnStatus")
    public void setMsisdnStatus(String msisdnStatus) {
        this.msisdnStatus = msisdnStatus;
    }

    @JsonProperty("descriptionStatus")
    public String getDescriptionStatus() {
        return descriptionStatus;
    }

    @JsonProperty("descriptionStatus")
    public void setDescriptionStatus(String descriptionStatus) {
        this.descriptionStatus = descriptionStatus;
    }

    @JsonProperty("hasPromo")
    public String getHasPromo() {
        return hasPromo;
    }

    @JsonProperty("hasPromo")
    public void setHasPromo(String hasPromo) {
        this.hasPromo = hasPromo;
    }

    @JsonProperty("lastTicketAmount")
    public String getLastTicketAmount() {
        return lastTicketAmount;
    }

    @JsonProperty("lastTicketAmount")
    public void setLastTicketAmount(String lastTicketAmount) {
        this.lastTicketAmount = lastTicketAmount;
    }

    @JsonProperty("freeUnits")
    public FreeUnitsSelf getFreeUnits() {
        return freeUnitsSelf;
    }

    @JsonProperty("freeUnits")
    public void setFreeUnitsSelf(FreeUnitsSelf freeUnitsSelf) {
        this.freeUnitsSelf = freeUnitsSelf;
    }

    @JsonProperty("sms")
    public Sms getSms() {
        return sms;
    }

    @JsonProperty("sms")
    public void setSms(Sms sms) {
        this.sms = sms;
    }

    @JsonProperty("minutes")
    public Minutes getMinutes() {
        return minutes;
    }

    @JsonProperty("minutes")
    public void setMinutes(Minutes minutes) {
        this.minutes = minutes;
    }

    @JsonProperty("freeUnitDetails")
    public List<FreeUnitDetail> getFreeUnitDetails() {
        return freeUnitDetails;
    }

    @JsonProperty("freeUnitDetails")
    public void setFreeUnitDetails(List<FreeUnitDetail> freeUnitDetails) {
        this.freeUnitDetails = freeUnitDetails;
    }


}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "name",
        "detailOfferings"
})
 class FreeUnitDetail {

    @JsonProperty("name")
    private String name;
    @JsonProperty("detailOfferings")
    private List<DetailOfferingSelf> detailOfferingsSelf = null;


    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("detailOfferings")
    public List<DetailOfferingSelf> getDetailOfferings() {
        return detailOfferingsSelf;
    }

    @JsonProperty("detailOfferings")
    public void setDetailOfferings(List<DetailOfferingSelf> detailOfferingsSelf) {
        this.detailOfferingsSelf = detailOfferingsSelf;
    }


}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "offeringId",
        "initialAmt",
        "unusedAmt",
        "effectiveDate",
        "expireDate",
        "remainder",
        "unlimited"
})
 class DetailOfferingSelf {

    @JsonProperty("offeringId")
    private String offeringId;
    @JsonProperty("initialAmt")
    private String initialAmt;
    @JsonProperty("unusedAmt")
    private String unusedAmt;
    @JsonProperty("effectiveDate")
    private String effectiveDate;
    @JsonProperty("expireDate")
    private String expireDate;
    @JsonProperty("remainder")
    private Integer remainder;
    @JsonProperty("unlimited")
    private String unlimited;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("offeringId")
    public String getOfferingId() {
        return offeringId;
    }

    @JsonProperty("offeringId")
    public void setOfferingId(String offeringId) {
        this.offeringId = offeringId;
    }

    @JsonProperty("initialAmt")
    public String getInitialAmt() {
        return initialAmt;
    }

    @JsonProperty("initialAmt")
    public void setInitialAmt(String initialAmt) {
        this.initialAmt = initialAmt;
    }

    @JsonProperty("unusedAmt")
    public String getUnusedAmt() {
        return unusedAmt;
    }

    @JsonProperty("unusedAmt")
    public void setUnusedAmt(String unusedAmt) {
        this.unusedAmt = unusedAmt;
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

    @JsonProperty("remainder")
    public Integer getRemainder() {
        return remainder;
    }

    @JsonProperty("remainder")
    public void setRemainder(Integer remainder) {
        this.remainder = remainder;
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