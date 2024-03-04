package com.topostechnology.rest.client.response;


import com.fasterxml.jackson.annotation.JsonInclude;
		import com.fasterxml.jackson.annotation.JsonProperty;
		import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
		"msisdn",
		"brand",
		"model",
		"imei",
		"deviceFeatures"
})
public class SearchResponse {

	@JsonProperty("msisdn")
	private String msisdn;
	@JsonProperty("brand")
	private String brand;
	@JsonProperty("model")
	private String model;
	@JsonProperty("imei")
	private ImeiInformation imeidevice;
	@JsonProperty("deviceFeatures")
	private Device device;

	@JsonProperty("msisdn")
	public String getMsisdn() {
		return msisdn;
	}

	@JsonProperty("msisdn")
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	@JsonProperty("brand")
	public String getBrand() {
		return brand;
	}

	@JsonProperty("brand")
	public void setBrand(String brand) {
		this.brand = brand;
	}

	@JsonProperty("model")
	public String getModel() {
		return model;
	}

	@JsonProperty("model")
	public void setModel(String model) {
		this.model = model;
	}

	@JsonProperty("imei")
	public ImeiInformation getImeidevice() {
		return imeidevice;
	}

	@JsonProperty("imei")
	public void setImeidevice(ImeiInformation imeidevice) {
		this.imeidevice = imeidevice;
	}

	@JsonProperty("deviceFeatures")
	public Device getDevice() {
		return device;
	}

	@JsonProperty("deviceFeatures")
	public void setDevice(Device device) {
		this.device = device;
	}

}


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
		"volteCapable",
		"supportedBearers",
		"radioStack",
		"deviceType",
		"os",
		"band28"
})
 class Device {

	@JsonProperty("volteCapable")
	private String volteCapable;
	@JsonProperty("supportedBearers")
	private String supportedBearers;
	@JsonProperty("radioStack")
	private String radioStack;
	@JsonProperty("deviceType")
	private String deviceType;
	@JsonProperty("os")
	private String os;
	@JsonProperty("band28")
	private String band28;

	@JsonProperty("volteCapable")
	public String getVolteCapable() {
		return volteCapable;
	}

	@JsonProperty("volteCapable")
	public void setVolteCapable(String volteCapable) {
		this.volteCapable = volteCapable;
	}

	@JsonProperty("supportedBearers")
	public String getSupportedBearers() {
		return supportedBearers;
	}

	@JsonProperty("supportedBearers")
	public void setSupportedBearers(String supportedBearers) {
		this.supportedBearers = supportedBearers;
	}

	@JsonProperty("radioStack")
	public String getRadioStack() {
		return radioStack;
	}

	@JsonProperty("radioStack")
	public void setRadioStack(String radioStack) {
		this.radioStack = radioStack;
	}

	@JsonProperty("deviceType")
	public String getDeviceType() {
		return deviceType;
	}

	@JsonProperty("deviceType")
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	@JsonProperty("os")
	public String getOs() {
		return os;
	}

	@JsonProperty("os")
	public void setOs(String os) {
		this.os = os;
	}

	@JsonProperty("band28")
	public String getBand28() {
		return band28;
	}

	@JsonProperty("band28")
	public void setBand28(String band28) {
		this.band28 = band28;
	}

}


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
		"imei",
		"status",
		"homologated"
})
 class ImeiInformation {

	@JsonProperty("imei")
	private String imei;
	@JsonProperty("status")
	private String status;
	@JsonProperty("homologated")
	private String homologated;

	@JsonProperty("imei")
	public String getImei() {
		return imei;
	}

	@JsonProperty("imei")
	public void setImei(String imei) {
		this.imei = imei;
	}

	@JsonProperty("status")
	public String getStatus() {
		return status;
	}

	@JsonProperty("status")
	public void setStatus(String status) {
		this.status = status;
	}

	@JsonProperty("homologated")
	public String getHomologated() {
		return homologated;
	}

	@JsonProperty("homologated")
	public void setHomologated(String homologated) {
		this.homologated = homologated;
	}

}