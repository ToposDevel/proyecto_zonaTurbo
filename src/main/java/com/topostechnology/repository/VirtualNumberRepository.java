package com.topostechnology.repository;

import com.topostechnology.domain.VirtualNumber;
import org.springframework.stereotype.Repository;

@Repository
public interface VirtualNumberRepository extends BaseRepository<VirtualNumber, Long> {

    VirtualNumber findByNumber(String virrtualNumber);
}
