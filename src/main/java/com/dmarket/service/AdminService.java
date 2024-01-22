package com.dmarket.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {
    //조회가 아닌 메서드들은 꼭 @Transactional 넣어주세요 (CUD, 입력/수정/삭제)
}
