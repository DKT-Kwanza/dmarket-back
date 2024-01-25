package com.dmarket.service;

import com.dmarket.constant.MileageReqState;
import com.dmarket.domain.user.MileageReq;
import com.dmarket.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dmarket.domain.board.*;
import com.dmarket.dto.response.*;
import com.dmarket.repository.board.*;
import com.dmarket.repository.user.*;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {
    // 조회가 아닌 메서드들은 꼭 @Transactional 넣어주세요 (CUD, 입력/수정/삭제)
    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;
    private final MileageReqRepository mileageReqRepository;

    @Transactional
    public void deleteUserByUserId(Long userId) {
        userRepository.deleteByUserId(userId);
    }

    public List<UserResDto> getUsersFindByDktNum(Integer userDktNum) {
        return userRepository.getUsersFindByDktNum(userDktNum);
    }

    public List<NoticeResDto> getNotices() {
        return noticeRepository.getNotices();
    }
    @Transactional
    public void postNotice(Long userId, String noticeTitle, String noticeContents) {
        Notice notice = Notice.builder()
                .userId(userId)
                .noticeTitle(noticeTitle)
                .noticeContents(noticeContents)
                .build();
        noticeRepository.save(notice);
    }

    // 마일리지 충전 요청 처리
    @Transactional
    public void approveMileageReq(Long mileageReqId, boolean request){
        MileageReq mileageReq = findMileageReqById(mileageReqId);
        if(request){
            mileageReq.updateState(MileageReqState.APPROVAL);
            User user = findUserById(mileageReq.getUserId());
            user.updateMileage(mileageReq.getMileageReqAmount());
        } else {
            mileageReq.updateState(MileageReqState.REFUSAL);
        }
    }

    @Transactional
    public void deleteNoticeByNoticeId(Long noticeId) {
        noticeRepository.deleteByNoticeId(noticeId);
    }

    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원 입니다."));
    }
    public MileageReq findMileageReqById(Long mileageReqId) {
        return mileageReqRepository.findById(mileageReqId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 마일리지 요청 입니다."));
    }
}
