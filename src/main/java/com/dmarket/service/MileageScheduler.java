package com.dmarket.service;

import com.dmarket.domain.user.Mileage;
import com.dmarket.repository.user.MileageRepository;
import com.dmarket.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MileageScheduler {
    private final UserRepository userRepository;
    private final MileageRepository mileageRepository;
    private static final Integer INIT_MIEAGE = 1200000;

    @Transactional
    @Scheduled(cron = "0 0 0 1 1 ?", zone = "Asia/Seoul")
    public void mileageAutoCharge() {
        // 마일리지 초기화
        userRepository.resetUserMileage(INIT_MIEAGE);

        // 사용자 번호 조회
        List<Long> users = userRepository.findAllUserId();

        // 마일리지 사용 내역에 자동충전 기록 추가
        for (Long user : users){
            Mileage mileage = Mileage.MileageAutoCharge(user);
            mileageRepository.save(mileage);
        }

        log.info("마일리지 초기화");
    }
}
