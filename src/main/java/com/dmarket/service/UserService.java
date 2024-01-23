package com.dmarket.service;

import com.dmarket.domain.user.User;
import com.dmarket.dto.request.JoinReqDto;
import com.dmarket.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    //조회가 아닌 메서드들은 꼭 @Transactional 넣어주세요 (CUD, 입력/수정/삭제)

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입
     * @Return userId
     */
    @Transactional
    public Long join(JoinReqDto dto) {

        User user = User.builder()
                .userEmail(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .userDktNum(dto.getDktNum())
                .userName(dto.getName())
                .userPhoneNum(dto.getPhoneNum())
                .userJoinDate(dto.getJoinDate())
                .userPostalCode(dto.getPostalCode())
                .userAddress(dto.getUserAddress())
                .userAddressDetail(dto.getUserAddressDetail())
                .build();

        userRepository.save(user);

        return user.getUserId();
    }

    public void verifyJoin(JoinReqDto dto) {

        String regExp = "^[a-zA-Z0-9!@#$%^]*$";
        String userEmail = dto.getEmail();
        String password = dto.getPassword();
        Integer userDktNum = dto.getDktNum();

        //이메일이 gachon.ac.kr로 끝나야 함
        if (!userEmail.endsWith("gachon.ac.kr")) {
            throw new IllegalArgumentException("이메일이 gachon.ac.kr로 끝나지 않습니다.");
        }

        //비밀번호 특수문자 모두 포함
        if (!password.matches(regExp)) {
            throw new IllegalArgumentException("비밀번호는 영문자, 숫자, 특수문자(!@#$%^)가 포함되어야 합니다.");
        }

        //이메일, 사원번호 겹치면 안됨
        if (existByUserEmail(userEmail) || existByUserDktNum(userDktNum)) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    public boolean existByUserEmail(String userEmail) {
        return userRepository.existsByUserEmail(userEmail);
    }

    public boolean existByUserDktNum(Integer userDktNum) {
        return userRepository.existsByUserDktNum(userDktNum);
    }
}
