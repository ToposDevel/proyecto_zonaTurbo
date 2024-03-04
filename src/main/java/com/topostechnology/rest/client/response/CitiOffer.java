package com.topostechnology.rest.client.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "offerId",
        "salesPrice",
        "offerName",
        "cname",
        "description"
})
public class CitiOffer {

    @JsonProperty("offerId")
    private String offerId;
    @JsonProperty("salesPrice")
    private String salesPrice;
    @JsonProperty("offerName")
    private String offerName;
    @JsonProperty("offerNamePrice")
    private String offerNamePrice;
    @JsonProperty("cname")
    private String cname;
    @JsonProperty("description")
    private String description;

    public CitiOffer() {
    }

    public CitiOffer(String offerId, String salesPrice, String offerName, String cname, String description) {
        super();
        this.offerId = offerId;
        this.salesPrice = salesPrice;
        this.offerName = offerName;
        this.cname = cname;
        this.description = description;
    }

    @JsonProperty("offerId")
    public String getOfferId() {
        return offerId;
    }

    @JsonProperty("offerId")
    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    @JsonProperty("salesPrice")
    public String getSalesPrice() {
        return salesPrice;
    }

    @JsonProperty("salesPrice")
    public void setSalesPrice(String salesPrice) {
        this.salesPrice = salesPrice;
    }

    @JsonProperty("offerName")
    public String getOfferName() {
        return offerName;
    }

    @JsonProperty("offerName")
    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }

    @JsonProperty("cname")
    public String getCname() {
        return cname;
    }

    @JsonProperty("cname")
    public void setCname(String cname) {
        this.cname = cname;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

	public String getOfferNamePrice() {
		return offerNamePrice;
	}

	public void setOfferNamePrice(String offerNamePrice) {
		this.offerNamePrice = offerNamePrice;
	}

}
