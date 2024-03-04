package com.topostechnology.rest.client.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TurboOfficeUsersResponse  extends GeneralResponse {
	
	List<TurboOfficeUserData> turboOfficeUserDataList;
    
}
