package com.topostechnology.rest.client.response;

public class SubscribersActivateRequest {
    private String offeringId;
    private String address;
    private String startEffectiveDate;
    private String expireEffectiveDate;
    private String scheduleDate;


    // Getter Methods

    public String getOfferingId() {
        return offeringId;
    }

    public String getAddress() {
        return address;
    }

    public String getStartEffectiveDate() {
        return startEffectiveDate;
    }

    public String getExpireEffectiveDate() {
        return expireEffectiveDate;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    // Setter Methods

    public void setOfferingId(String offeringId) {
        this.offeringId = offeringId;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setStartEffectiveDate(String startEffectiveDate) {
        this.startEffectiveDate = startEffectiveDate;
    }

    public void setExpireEffectiveDate(String expireEffectiveDate) {
        this.expireEffectiveDate = expireEffectiveDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }
}
