package com.topostechnology.rest.client.request;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({

        "be_id",
        "password",
        "msisdn",
        "offerings",
        "startEffectiveDate",
        "expireEffectiveDate"
})
public class PurchaseRequest {


    @JsonProperty("be_id")
    @NotEmpty(message = "El be_id es requerido")
    private String beId;
    @JsonProperty("password")
    private String password;
    @JsonProperty("msisdn")
    @NotEmpty(message = "El msisdn es requerido")
    private String msisdn;
    @JsonProperty("offerings")
    @NotEmpty(message = "La oferta es requerida")
    private List<@Valid String> offerings = null;
    @JsonProperty("startEffectiveDate")
    private String startEffectiveDate;
    @JsonProperty("expireEffectiveDate")
    private String expireEffectiveDate;
    private String lastError;


    @JsonProperty("be_id")
    public String getBeId() {
        return beId;
    }

    @JsonProperty("be_id")
    public void setBeId(String beId) {
        this.beId = beId;
    }

    @JsonProperty("password")
    public String getPassword() {
        return password;
    }

    @JsonProperty("password")
    public void setPassword(String password) {
        this.password = password;
    }

    @JsonProperty("msisdn")
    public String getMsisdn() {
        return msisdn;
    }

    @JsonProperty("msisdn")
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    @JsonProperty("offerings")
    public List<String> getOfferings() {
        return offerings;
    }

    @JsonProperty("offerings")
    public void setOfferings(List<String> offerings) {
        this.offerings = offerings;
    }

    @JsonProperty("startEffectiveDate")
    public String getStartEffectiveDate() {
        return startEffectiveDate;
    }

    @JsonProperty("startEffectiveDate")
    public void setStartEffectiveDate(String startEffectiveDate) {
        this.startEffectiveDate = startEffectiveDate;
    }

    @JsonProperty("expireEffectiveDate")
    public String getExpireEffectiveDate() {
        return expireEffectiveDate;
    }

    @JsonProperty("expireEffectiveDate")
    public void setExpireEffectiveDate(String expireEffectiveDate) {
        this.expireEffectiveDate = expireEffectiveDate;
    }


    @Override
    public String toString() {
        return "PurchaseRequest{" +

                ", beId='" + beId + '\'' +
                ", password='" + password + '\'' +
                ", msisdn='" + msisdn + '\'' +
                ", offerings=" + offerings +
                ", startEffectiveDate='" + startEffectiveDate + '\'' +
                ", expireEffectiveDate='" + expireEffectiveDate + '\'' +
                '}';
    }

}
