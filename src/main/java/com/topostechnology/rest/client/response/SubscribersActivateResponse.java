package com.topostechnology.rest.client.response;

public class SubscribersActivateResponse {


    private String msisdn;
    private String effectiveDate;
    private String offeringId;
    private String startEffectiveDate;
    private String expireEffectiveDate;
    private Order order;

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getOfferingId() {
        return offeringId;
    }

    public void setOfferingId(String offeringId) {
        this.offeringId = offeringId;
    }

    public String getStartEffectiveDate() {
        return startEffectiveDate;
    }

    public void setStartEffectiveDate(String startEffectiveDate) {
        this.startEffectiveDate = startEffectiveDate;
    }

    public String getExpireEffectiveDate() {
        return expireEffectiveDate;
    }

    public void setExpireEffectiveDate(String expireEffectiveDate) {
        this.expireEffectiveDate = expireEffectiveDate;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
