package com.topostechnology.rest.client.response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseError implements Serializable {

	private static final long serialVersionUID = -1951080497566320911L;
	
		@JsonProperty("errorCode")
    private String errorCode;
    @JsonProperty("description")
    private String description;
    @JsonProperty("detail")
    private String detail;

    public ResponseError() {
    }

    public ResponseError(String errorCode, String description, String detail) {
        this.errorCode = errorCode;
        this.description = description;
        this.detail = detail;
    }

    @JsonProperty("errorCode")
    public String getErrorCode() {
        return errorCode;
    }

    @JsonProperty("errorCode")
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("detail")
    public String getDetail() {
        return detail;
    }

    @JsonProperty("detail")
    public void setDetail(String detail) {
        this.detail = detail;
    }
}
