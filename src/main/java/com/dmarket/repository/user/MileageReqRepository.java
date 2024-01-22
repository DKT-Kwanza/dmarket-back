package com.dmarket.repository.user;

import com.dmarket.domain.user.MileageReq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MileageReqRepository extends JpaRepository<MileageReq, Long> {
}
