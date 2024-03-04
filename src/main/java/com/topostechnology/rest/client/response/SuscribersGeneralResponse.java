package com.topostechnology.rest.client.response;

public class SuscribersGeneralResponse {



    private String msisdnPorted;
    private String msisdn;
    private String effectiveDate;
    private Order order;


    public String getMsisdnPorted() {
        return msisdnPorted;
    }

    public void setMsisdnPorted(String msisdnPorted) {
        this.msisdnPorted = msisdnPorted;
    }
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

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
