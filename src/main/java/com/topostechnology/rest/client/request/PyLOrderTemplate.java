package com.topostechnology.rest.client.request;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "line_items",
        "currency",
        "customer_info"
})
public class PyLOrderTemplate {

//    @JsonProperty("line_items")
//    private List<LineItem> lineItems = new ArrayList<>();
    @JsonProperty("currency")
    private String currency;
//    @JsonProperty("customer_info")
//    private CustomerInfo customerInfo;

//    @JsonProperty("line_items")
//    public List<LineItem> getLineItems() {
//        return lineItems;
//    }
//
//    @JsonProperty("line_items")
//    public void setLineItems(List<LineItem> lineItems) {
//        this.lineItems = lineItems;
//    }

    @JsonProperty("currency")
    public String getCurrency() {
        return currency;
    }

    @JsonProperty("currency")
    public void setCurrency(String currency) {
        this.currency = currency;
    }

//    @JsonProperty("customer_info")
//    public CustomerInfo getCustomerInfo() {
//        return customerInfo;
//    }
//
//    @JsonProperty("customer_info")
//    public void setCustomerInfo(CustomerInfo customerInfo) {
//        this.customerInfo = customerInfo;
//    }

}