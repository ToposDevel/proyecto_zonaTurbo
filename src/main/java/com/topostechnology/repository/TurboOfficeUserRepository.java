package com.topostechnology.repository;

import com.topostechnology.domain.TurboOfficeUser;
import org.springframework.stereotype.Repository;

@Repository
public interface  TurboOfficeUserRepository extends BaseRepository<TurboOfficeUser, Long> {

    TurboOfficeUser findByAssociatedNumber(String associatedNumber);
}
