package com.topostechnology.repository;

import org.springframework.stereotype.Repository;

import com.topostechnology.domain.User;

@Repository
public interface UserRepository extends BaseRepository<User, Long> {
	

    User findByUserName(String userName);
	
	User findByUserNameAndEmail(String cellphoneNumber, String email);


}
