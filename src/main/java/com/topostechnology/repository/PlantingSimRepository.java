package com.topostechnology.repository;

import com.topostechnology.domain.PlantingSim;

public interface PlantingSimRepository  extends BaseRepository<PlantingSim, Long>{

	PlantingSim findByCellphoneNumberAndActive(String cellphoneNumber, boolean active);
	PlantingSim findByIccidAndActive(String iccid, boolean active);
}
