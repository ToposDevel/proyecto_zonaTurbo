package com.topostechnology.rest.client.request;

import java.util.List;

import com.topostechnology.rest.client.response.GeneralResponse;
import com.topostechnology.rest.client.response.TurboOfficePlanDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TurboOfficePlanResponse extends GeneralResponse {
	
	private List<TurboOfficePlanDto>  turboOfficePlans;

}
