package com.topostechnology.rest.client.response;

import java.util.List;
        import com.fasterxml.jackson.annotation.JsonInclude;
        import com.fasterxml.jackson.annotation.JsonProperty;
        import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "transaction_id",
        "response_code",
        "reponse_description",
        "be_id",
        "coperator",
        "offers"
})
public class CitiConsultingOffersResponse {

    @JsonProperty("transaction_id")

    private String transactionId;
    @JsonProperty("response_code")
    private Integer responseCode;
    @JsonProperty("reponse_description")
    private String reponse_description;

    @JsonProperty("be_id")
    private String beId;
    @JsonProperty("coperator")
    private String coperator;
    @JsonProperty("offers")
    private List<CitiOffer> offers = null;

    public CitiConsultingOffersResponse() {
    }

    public CitiConsultingOffersResponse(Integer responseCode, String reponse_description) {
        this.responseCode = responseCode;
        this.reponse_description = reponse_description;
    }

    @JsonProperty("transaction_id")
    public String getTransactionId() {
        return transactionId;
    }

    @JsonProperty("transaction_id")
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    @JsonProperty("response_code")
    public Integer getResponseCode() {
        return responseCode;
    }

    @JsonProperty("response_code")
    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    @JsonProperty("be_id")
    public String getBeId() {
        return beId;
    }

    @JsonProperty("be_id")
    public void setBeId(String beId) {
        this.beId = beId;
    }

    @JsonProperty("coperator")
    public String getCoperator() {
        return coperator;
    }

    @JsonProperty("coperator")
    public void setCoperator(String coperator) {
        this.coperator = coperator;
    }

    @JsonProperty("offers")
    public List<CitiOffer> getOffers() {
        return offers;
    }

    @JsonProperty("offers")
    public void setOffers(List<CitiOffer> offers) {
        this.offers = offers;
    }

    @JsonProperty("reponse_description")
    public String getReponse_description() {
        return reponse_description;
    }
    @JsonProperty("reponse_description")
    public void setReponse_description(String reponse_description) {
        this.reponse_description = reponse_description;
    }

}