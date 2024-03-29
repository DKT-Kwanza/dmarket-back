package com.dmarket.repository.user;

import com.dmarket.domain.user.MileageReq;
import com.dmarket.dto.common.MileageCommonDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MileageReqRepository extends JpaRepository<MileageReq, Long> {
    @Query("select new com.dmarket.dto.common.MileageCommonDto$MileageReqListDto" +
            "(m.mileageReqId, m.mileageReqDate, m.userId, u.userName, u.userEmail, m.mileageReqReason, m.mileageReqAmount, m.mileageReqState) " +
            "from MileageReq m " +
            "join User u on u.userId = m.userId " +
            "where m.mileageReqState = 'PROCESSING'")
    Page<MileageCommonDto.MileageReqListDto> findAllByProcessing(Pageable pageable);

    @Query("select new com.dmarket.dto.common.MileageCommonDto$MileageReqListDto" +
            "(m.mileageReqId, m.mileageReqDate, m.userId, u.userName, u.userEmail, m.mileageReqReason, m.mileageReqAmount, m.mileageReqState) " +
            "from MileageReq m " +
            "join User u on u.userId = m.userId " +
            "where m.mileageReqState != 'PROCESSING'")
    Page<MileageCommonDto.MileageReqListDto> findAllByProcessed(Pageable pageable);
}
