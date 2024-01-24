package com.dmarket.service;

import com.dmarket.domain.user.User;
import com.dmarket.dto.request.JoinReqDto;
import com.dmarket.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    //조회가 아닌 메서드들은 꼭 @Transactional 넣어주세요 (CUD, 입력/수정/삭제)

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final RedisService redisService;

    @Value("${spring.mail.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;

    /**
     * 회원가입
     * @Return userId
     */
    @Transactional
    public Long join(JoinReqDto dto) {

        User user = User.builder()
                .userEmail(dto.getUserEmail())
                .password(passwordEncoder.encode(dto.getUserPassword()))
                .userDktNum(dto.getUserDktNum())
                .userName(dto.getUserName())
                .userPhoneNum(dto.getUserPhoneNum())
                .userJoinDate(dto.getUserJoinDate())
                .userPostalCode(dto.getUserPostalCode())
                .userAddress(dto.getUserAddress())
                .userAddressDetail(dto.getUserDetailedAddress())
                .build();

        userRepository.save(user);

        return user.getUserId();
    }

    //회원가입 유효성 확인
    public void verifyJoin(JoinReqDto dto) {

        String regExp = "^[a-zA-Z0-9!@#$%^]*$";
        String userEmail = dto.getUserEmail();
        String password = dto.getUserPassword();
        Integer userDktNum = dto.getUserDktNum();

        isValidEmail(userEmail);

        //비밀번호 특수문자 모두 포함
        if (!password.matches(regExp)) {
            throw new IllegalArgumentException("비밀번호는 영문자, 숫자, 특수문자(!@#$%^)가 포함되어야 합니다.");
        }

        //사원번호 겹치면 안됨
        if (existByUserDktNum(userDktNum)) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }
    }

    public void isValidEmail(String email) {

        //이메일이 gachon.ac.kr로 끝나야 함
        if (!email.endsWith("gachon.ac.kr")) {
            throw new IllegalArgumentException("이메일이 gachon.ac.kr로 끝나지 않습니다.");
        }
        //이메일이 겹치면 안 됨
        if (existByUserEmail(email)) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }
    }

    public void sendCodeToEmail(String toEmail) {
//        if (existByUserEmail(toEmail)) throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        isValidEmail(toEmail);
        String title = "Dmarket 회원가입 인증번호";
        String authCode = createCode();
        mailService.sendEmail(toEmail, title, authCode);

        // 이메일 인증 요청 시 인증 번호 Redis에 저장 ( key = auth:email:abc@gachon.ac.kr / value = 000000 )
        redisService.setValues("auth:email:" + toEmail, authCode, Duration.ofMillis(this.authCodeExpirationMillis));
    }

    /* Redis 구현 후 완성
    public EmailVerificationResult verifiedCode(String email, String authCode) {
        this.checkDuplicatedEmail(email);
        String redisAuthCode = redisService.getValues(AUTH_CODE_PREFIX + email);
        boolean authResult = redisService.checkExistsValue(redisAuthCode) && redisAuthCode.equals(authCode);

        return EmailVerificationResult.of(authResult);
    }
    */

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


    private String createCode() {
        int length = 6;
        try {
            SecureRandom random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            log.warn("UserService.createCode()");
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
    }
}
