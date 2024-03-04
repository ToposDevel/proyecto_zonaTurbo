package com.topostechnology.mapper;

import com.topostechnology.domain.Phone;
import com.topostechnology.domain.User;
import com.topostechnology.model.UserModel;

public class UserMapper {
	
	public static User convertUserModelToUser(UserModel userModel) {
		User user = new User();
		user.setName(userModel.getName());
		user.setLastName(userModel.getLastName());
		user.setSecodLastName(userModel.getSecondLastName());
		user.setEmail(userModel.getEmail());
		user.setPassword(userModel.getPassword());
		user.setId(userModel.getId());
		user.setLoginWithImei(userModel.isLoginWithImei());
		user.setUserName(userModel.isLoginWithImei() ? userModel.getImei() : userModel.getCellphoneNumber());
		return user;
	}
	
	public static UserModel convertUserToUserModel(User user) {
		UserModel userModel = new UserModel();
		userModel.setName(user.getName());
		userModel.setLastName(user.getLastName());
		userModel.setSecondLastName(user.getSecodLastName());
		userModel.setEmail(user.getEmail());
		userModel.setPassword("");
		userModel.setPasswordConfirmation("");
		userModel.setActive(user.isActive());
		if(user.getPhones() != null && user.getPhones().size() > 0){
			Phone phone = user.getPhones().get(0);
			userModel.setCellphoneNumber(phone.getCellphoneNumber());
		}
		
		userModel.setId(user.getId());
		userModel.setUserName(user.getUserName());
		userModel.setRoleName(user.getRole().getName());
		userModel.setLoginWithImei(user.isLoginWithImei());
		return userModel;
	}

}
