package com.topostechnology.rest.client.response;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileDataResponse {
	
	private String cellphoneNumber;
	private ProfileInformation information;
	private ProfileStatus status;
	private ArrayList<ProfileFreeUnit> freeUnits;
	private ProfilePrimaryOffering primaryOffering;

}
