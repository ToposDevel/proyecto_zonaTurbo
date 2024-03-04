package com.topostechnology.rest.client.response;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileFreeUnit {
	private String name;
	private ProfileTotalConsumption freeUnit;
	private ArrayList<ProfileDetailOffering> detailOfferings;

}
