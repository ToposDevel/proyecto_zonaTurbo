package com.topostechnology.rest.client.response;





import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({

        "reponse_code",
        "reponse_description",
        "msisdn",
        "effectiveDate",
        "offerings",
        "startEffectiveDate",
        "expireEffectiveDate",
        "order"
})
public class PurchaseResponse  {


    @JsonProperty("response_code")
    private Integer responseCode;
    @JsonProperty("reponse_description")
    private String reponse_description;
    @JsonProperty("msisdn")
    private String msisdn;
    @JsonProperty("effectiveDate")
    private String effectiveDate;
    @JsonProperty("offerings")
    private List<String> offerings = null;
    @JsonProperty("startEffectiveDate")
    private String startEffectiveDate;
    @JsonProperty("expireEffectiveDate")
    private String expireEffectiveDate;
    @JsonProperty("order")
    private Order order;
    
    private AltanActionResponse altanActionResponse;


    public AltanActionResponse getAltanActionResponse() {
		return altanActionResponse;
	}

	public void setAltanActionResponse(AltanActionResponse altanActionResponse) {
		this.altanActionResponse = altanActionResponse;
	}

	public PurchaseResponse() {
    }

    public PurchaseResponse(Integer responseCode, String reponse_description) {

        this.responseCode = responseCode;
        this.reponse_description = reponse_description;
    }




    @JsonProperty("msisdn")
    public String getMsisdn() {
        return msisdn;
    }

    @JsonProperty("msisdn")
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    @JsonProperty("effectiveDate")
    public String getEffectiveDate() {
        return effectiveDate;
    }

    @JsonProperty("effectiveDate")
    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
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

    @JsonProperty("order")
    public Order getOrder() {
        return order;
    }

    @JsonProperty("order")
    public void setOrder(Order order) {
        this.order = order;
    }


    @JsonProperty("reponse_description")
    public String getReponse_description() {
        return reponse_description;
    }
    @JsonProperty("reponse_description")
    public void setReponse_description(String reponse_description) {
        this.reponse_description = reponse_description;
    }

    @JsonProperty("response_code")
    public Integer getResponseCode() {
        return responseCode;
    }
    @JsonProperty("response_code")
    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

}
