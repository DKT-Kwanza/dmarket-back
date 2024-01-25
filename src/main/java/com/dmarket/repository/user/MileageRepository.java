package com.dmarket.repository.user;

import com.dmarket.domain.user.Mileage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MileageRepository extends JpaRepository<Mileage, Long> {
    // 사용자의 마일리지 내역 조회
    Page<Mileage> findByUserId(Pageable pageable, Long userId);
}
