package com.dmarket.service;

import com.dmarket.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MileageScheduler {
    private final UserRepository userRepository;
    private static final Integer INIT_MIEAGE = 1200000;

    @Transactional
    @Scheduled(cron = "0 0 0 1 1 ?", zone = "Asia/Seoul")
    public void mileageAutoCharge() {
        userRepository.resetUserMileage(INIT_MIEAGE);
        log.info("마일리지 초기화");
    }
}