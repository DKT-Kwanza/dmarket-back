package com.dmarket.repository.user;

import com.dmarket.domain.user.Mileage;
import com.dmarket.dto.common.MileageCommonDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MileageRepository extends JpaRepository<Mileage, Long> {
    // 사용자의 마일리지 내역 조회
    @Query("select new com.dmarket.dto.common.MileageCommonDto$MileageDto" +
            "(m.mileageDate, m.mileageInfo, m.changeMileage, m.remainMileage) " +
            "from Mileage m " +
            "where m.userId = :userId")
    Page<MileageCommonDto.MileageDto> findByUserId(Pageable pageable, Long userId);
}
