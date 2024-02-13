package com.dmarket.integrationTest;

import com.dmarket.constant.MileageContents;
import com.dmarket.constant.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dmarket.TestUtility;
import com.dmarket.domain.board.Notice;
import com.dmarket.domain.user.Mileage;
import com.dmarket.domain.user.MileageReq;
import com.dmarket.domain.user.User;
import com.dmarket.dto.common.MileageCommonDto;
import com.dmarket.dto.common.UserCommonDto;
import com.dmarket.dto.request.NoticeReqDto;
import com.dmarket.dto.request.UserReqDto;
import com.dmarket.dto.response.AdminResDto;
import com.dmarket.dto.response.NoticeResDto;
import com.dmarket.dto.response.UserResDto;
import com.dmarket.jwt.JWTUtil;
import com.dmarket.repository.board.NoticeRepository;
import com.dmarket.repository.user.MileageRepository;
import com.dmarket.repository.user.MileageReqRepository;
import com.dmarket.repository.user.UserRepository;
import com.dmarket.service.AdminService;
import com.dmarket.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.util.*;
import java.util.stream.Collectors;

import java.time.LocalDate;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
public class AdminIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AdminService adminService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private MileageRepository mileageRepository;

    @MockBean
    private MileageReqRepository mileageReqRepository;

    @MockBean
    private NoticeRepository noticeRepository;

    @MockBean
    private JWTUtil jwtUtil;

    private String token;

    @BeforeEach
    public void setUp() {
        token = "Bearer " + createTestToken();
    }

    private String createTestToken() {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 60 * 60 * 1000); // 1시간 후 만료

        return JWT.create()
                .withIssuer("testIssuer")
                .withIssuedAt(now)
                .withExpiresAt(expiryDate)
                .withClaim("userId", 9)
                .withClaim("role", "ROLE_USER")
                .withClaim("email", "testEmail@gachon.ac.kr")
                .sign(Algorithm.HMAC512("testSecret"));
    }


    @Test
    @DisplayName("관리자 검색")
    public void getAdmins_withDktNum() throws Exception {
        User testUser = TestUtility.createTestUser();
        given(userRepository.findByUserDktNum(any(Integer.class))).willReturn(testUser);

        // 실제 요청을 보내어 검증
        mockMvc.perform(get("/api/admin/admin-users")
                .header("Authorization", token)
                .param("q", "1")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(testUser.getUserId()))
                .andExpect(jsonPath("$.data.userName").value(testUser.getUserName()))
                .andExpect(jsonPath("$.data.userEmail").value(testUser.getUserEmail()))
                .andExpect(jsonPath("$.data.userDktNum").value(testUser.getUserDktNum()))
                .andExpect(jsonPath("$.data.userRole").value(testUser.getUserRole().name()))
                .andExpect(jsonPath("$.data.userJoinDate").value(testUser.getUserJoinDate().toString()))
                .andDo(MockMvcRestDocumentation.document("get-admins-with-dktnum"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("전체 관리자 조회")
    public void getAdmins_withoutDktNum() throws Exception {
        User testGM = TestUtility.createTestGM();
        User testPM = TestUtility.createTestPM();
        User testSM = TestUtility.createTestSM();
        List<User> testManagers = List.of(testGM, testPM, testSM);
        given(userRepository.findAllByUserRoleIsNot(any(Role.class))).willReturn(testManagers);
        int gmCount = adminService.adminCount(testManagers, Role.ROLE_GM);
        int smCount = adminService.adminCount(testManagers, Role.ROLE_SM);
        int pmCount = adminService.adminCount(testManagers, Role.ROLE_PM);

        mockMvc.perform(get("/api/admin/admin-users")
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalManagerCount").value(testManagers.size()))
                .andExpect(jsonPath("$.data.gmcount").value(gmCount))
                .andExpect(jsonPath("$.data.smcount").value(smCount))
                .andExpect(jsonPath("$.data.pmcount").value(pmCount))
                .andDo(MockMvcRestDocumentation.document("get-admins-without-dktnum"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("권한 변경")
    public void changeRole() throws Exception {
        User testUser = TestUtility.createTestUser();
        Long userId = 1L;
        given(userRepository.findByUserId(any(Long.class))).willReturn(testUser);
        UserReqDto.ChangeRole newRole = new UserReqDto.ChangeRole();
        newRole.setNewRole("ROLE_GM");

        String newaccessToken = jwtUtil.createAccessJwt(userId, newRole.getNewRole(), testUser.getUserEmail());
        String newrefreshToken = jwtUtil.createRefreshJwt();

        mockMvc.perform(put("/api/admin/admin-users/" + userId)
                .header("Authorization", token)
                .content(objectMapper.writeValueAsString(newRole))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.data.accesstoken").value(newaccessToken))
                .andExpect(jsonPath("$.data.refreshtoken").value(newrefreshToken))
                .andExpect(jsonPath("$.data.userId").value(userId))
                .andExpect(jsonPath("$.data.role").value(newRole.toString()))
                .andDo(MockMvcRestDocumentation.document("change-role"))
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("사용자 조회")
    public void getUser() throws Exception {
        User testUser = TestUtility.createTestUser();
        List<User> testUserList = List.of(testUser);
        given(userRepository.getUsersFindByEmail(any(String.class))).willReturn(testUserList);
        List<UserResDto.Search> testUserResDtos = testUserList.stream()
                .map(UserResDto.Search::new)
                .collect(Collectors.toList());

        mockMvc.perform(get("/api/admin/admin-user")
                .header("Authorization", token)
                .param("q", "testEmail")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].userId").value(testUser.getUserId()))
                .andExpect(jsonPath("$.data[0].userName").value(testUser.getUserName()))
                .andExpect(jsonPath("$.data[0].userEmail").value(testUser.getUserEmail()))
                .andExpect(jsonPath("$.data[0].userDktNum").value(testUser.getUserDktNum()))
                .andExpect(jsonPath("$.data[0].userRole").value(testUser.getUserRole().name()))
                .andExpect(jsonPath("$.data[0].userJoinDate").value(testUser.getUserJoinDate().toString()))
                .andExpect(jsonPath("$.data", hasSize(testUserList.size())))
                .andDo(MockMvcRestDocumentation.document("get-user"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("사용자 삭제")
    public void deleteUser() throws Exception {
        Long userId = 1L;
        given(userRepository.existsById(any(Long.class))).willReturn(true);
        doNothing().when(userRepository).deleteByUserId(userId);

        mockMvc.perform(delete("/api/admin/users/" + userId)
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentation.document("delete-user"))
                .andDo(MockMvcResultHandlers.print());

        verify(userRepository, times(1)).deleteByUserId(userId);
    }

    @Test
    @DisplayName("마일리지 충전 요청/처리 내역 조회")
    public void getMileageRequests() throws Exception {
        MileageCommonDto.MileageReqListDto testMileageReq = TestUtility.createTestMileageReqDto();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "mileageReqDate"));
        Page<MileageCommonDto.MileageReqListDto> dtos;
        List<MileageCommonDto.MileageReqListDto> list = Arrays.asList(testMileageReq);
        dtos = new PageImpl<>(list, pageable, list.size());


        given(mileageReqRepository.findAllByProcessing(any(Pageable.class))).willReturn(dtos);

        mockMvc.perform(get("/api/admin/users/mileage-history")
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].mileageReqId").value(1))
                .andDo(MockMvcRestDocumentation.document("get-mileage-requests"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("마일리지 요청 승인")
    public void approveMileageReq() throws Exception {
        MileageReq testMileageReq = TestUtility.createTestMileageReq();
        given(mileageReqRepository.findById(any(Long.class))).willReturn(Optional.of(testMileageReq));
        User testUser = TestUtility.createTestUser();
        given(userRepository.findById(any(Long.class))).willReturn(Optional.of(testUser));
        Long mileageReqId = 1L;

        mockMvc.perform(put("/api/admin/users/mileage/approval/" + mileageReqId)
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentation.document("approve-mileage-req"))
                .andDo(MockMvcResultHandlers.print());

        verify(mileageRepository, times(1)).save(any(Mileage.class));
    }

    @Test
    @DisplayName("마일리지 요청 거부")
    public void refusalMileageReq() throws Exception {
        MileageReq testMileageReq = TestUtility.createTestMileageReq();
        given(mileageReqRepository.findById(any(Long.class))).willReturn(Optional.of(testMileageReq));
        User testUser = TestUtility.createTestUser();
        given(userRepository.findById(any(Long.class))).willReturn(Optional.of(testUser));
        Long mileageReqId = 1L;

        mockMvc.perform(put("/api/admin/users/mileage/refusal/" + mileageReqId)
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentation.document("refusal-mileage-req"))
                .andDo(MockMvcResultHandlers.print());

        verify(userRepository, times(1)).findById(any(Long.class));
    }

    @Test
    @DisplayName("공지사항 목록 조회")
    public void getNotices() throws Exception {
        NoticeResDto testDto = TestUtility.createTestNoticeResDto();
        Pageable pageable = PageRequest.of(0, 10);
        Page<NoticeResDto> dtos;
        List<NoticeResDto> list = Arrays.asList(testDto);
        dtos = new PageImpl<>(list, pageable, list.size());


        given(noticeRepository.getNotices(any(Pageable.class))).willReturn(dtos);

        mockMvc.perform(get("/api/admin/board/notices")
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].noticeTitle").value("testTitle"))
                .andDo(MockMvcRestDocumentation.document("get-notices"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("공지사항 작성")
    public void postNotice() throws Exception {
        NoticeReqDto testNoticeReqDto = TestUtility.createTestNoticeReqDto();
        Notice testNotice = TestUtility.createTestNotice();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Notice> dtos;
        List<Notice> list = Arrays.asList(testNotice);
        dtos = new PageImpl<>(list, pageable, list.size());

        given(noticeRepository.findAll(any(Pageable.class))).willReturn(dtos);

        mockMvc.perform(post("/api/admin/board/notice")
                .header("Authorization", token)
                .content(objectMapper.writeValueAsString(testNoticeReqDto))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].noticeTitle").value("testTitle"))
                .andDo(MockMvcRestDocumentation.document("post-notice"))
                .andDo(MockMvcResultHandlers.print());

        verify(noticeRepository, times(1)).save(any(Notice.class));
    }
}
