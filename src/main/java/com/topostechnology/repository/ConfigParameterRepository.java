package com.topostechnology.repository;

import com.topostechnology.domain.ConfigParameter;

public interface ConfigParameterRepository extends BaseRepository<ConfigParameter, Long>{
	
	
	ConfigParameter findByName(String name);

}
