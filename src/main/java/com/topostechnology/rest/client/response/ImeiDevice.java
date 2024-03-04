package com.topostechnology.rest.client.response;

import com.fasterxml.jackson.annotation.JsonInclude;
        import com.fasterxml.jackson.annotation.JsonProperty;
        import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "imei",
        "homologated",
        "blocked"
})
public class ImeiDevice {

    @JsonProperty("imei")
    private String imei;
    @JsonProperty("homologated")
    private String homologated;
    @JsonProperty("blocked")
    private String blocked;

    @JsonProperty("imei")
    public String getImei() {
        return imei;
    }

    @JsonProperty("imei")
    public void setImei(String imei) {
        this.imei = imei;
    }

    @JsonProperty("homologated")
    public String getHomologated() {
        return homologated;
    }

    @JsonProperty("homologated")
    public void setHomologated(String homologated) {
        this.homologated = homologated;
    }

    @JsonProperty("blocked")
    public String getBlocked() {
        return blocked;
    }

    @JsonProperty("blocked")
    public void setBlocked(String blocked) {
        this.blocked = blocked;
    }

}