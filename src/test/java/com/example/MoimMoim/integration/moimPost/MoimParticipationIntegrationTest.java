package com.example.MoimMoim.integration.moimPost;

import com.example.MoimMoim.domain.Member;
import com.example.MoimMoim.domain.MoimParticipation;
import com.example.MoimMoim.domain.MoimPost;
import com.example.MoimMoim.dto.moimParticipation.MoimParticipationListResponseDTO;
import com.example.MoimMoim.dto.moimParticipation.MoimParticipationRequestDTO;
import com.example.MoimMoim.dto.moimPost.MoimPostRequestDTO;
import com.example.MoimMoim.enums.Category;
import com.example.MoimMoim.enums.ParticipationStatus;
import com.example.MoimMoim.repository.MemberRepository;
import com.example.MoimMoim.repository.MoimParticipationRepository;
import com.example.MoimMoim.repository.MoimPostRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MoimParticipationIntegrationTest {


    private static final Logger log = LoggerFactory.getLogger(MapSearchIntegrationTest.class);
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MoimPostRepository moimPostRepository;

    @Autowired
    private MoimParticipationRepository moimParticipationRepository;

    /*
    * 1. 두 회원의 정보가 필요하다.
    * */

    // 모임 생성자 변수
    String hostEmail, hostPassword;
    String hostAuthToken;

    // 모임 참가자 변수
    String participantEmail, participantPassword;
    String participantAuthToken;


    // 멤버 객체
    Member hostMember;
    Member participantMember;

    // 모임 게시글 작성 DTO
    MoimPostRequestDTO moimPostRequestDTO;
    // DTO의 주소에 사용될 변수들
    private String title;
    private String address;
    private String roadAddress;
    private double mapx;
    private double mapy;

    // 모임 게시글 id
    long postId;
    // 참여 신청 아이디
    long participationId;

    // 참여 신청 DTO
    MoimParticipationRequestDTO moimParticipationRequestDTO
            = new MoimParticipationRequestDTO();


    @BeforeAll
    void setup() {
        hostEmail = "test@example.com";
        hostPassword = "Aa!12345";

        participantEmail = "test2@example.com";
        participantPassword = "Bb!12345";
    }

    @Test
    @Order(1)
    @DisplayName("모임주최자와 참가자 회원가입")
    void testSignup() throws Exception {

        String hostSignupRequestBody = """
                {
                    "email": "%s",
                    "password": "%s",
                    "phone": "010-1234-5678",
                    "name": "홍길동",
                    "gender": "남자",
                    "nickname": "길동이",
                    "birthday": "1995-08-15"
                }
                """.formatted(hostEmail, hostPassword);

        String participantSignupRequestBody = """
                {
                    "email": "%s",
                    "password": "%s",
                    "phone": "010-1234-5555",
                    "name": "김영희",
                    "gender": "여자",
                    "nickname": "영희",
                    "birthday": "1997-08-15"
                }
                """.formatted(participantEmail, participantPassword);

        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(hostSignupRequestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("message").value("회원가입이 완료되었습니다."));

        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(participantSignupRequestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("message").value("회원가입이 완료되었습니다."));

        Optional<Member> getHostMember = memberRepository.findByEmail(hostEmail);
        Optional<Member> getParticipantMember = memberRepository.findByEmail(participantEmail);

        assertThat(getHostMember).isNotEmpty();
        assertThat(getParticipantMember).isNotEmpty();

        hostMember = getHostMember.get();
        participantMember = getParticipantMember.get();


    }

    @Test
    @Order(2)
    @DisplayName("로그인")
    void testLogin() throws Exception {
        String hostLoginRequestBody = """
                {
                    "username": "%s",
                    "password": "%s"
                }
                """.formatted(hostEmail, hostPassword);

        String participantLoginRequestBody = """
                {
                    "username": "%s",
                    "password": "%s"
                }
                """.formatted(hostEmail, hostPassword);

        MvcResult hostLoginResult = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(hostLoginRequestBody))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult participantLoginResult = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(participantLoginRequestBody))
                .andExpect(status().isOk())
                .andReturn();

        // 토큰이 잘 생성 되었는가 확인
        hostAuthToken = hostLoginResult.getResponse().getHeader("Authorization");
        participantAuthToken = participantLoginResult.getResponse().getHeader("Authorization");
        assertThat(hostAuthToken).isNotNull();
        assertThat(participantAuthToken).isNotNull();
    }


    @Test
    @Order(3)
    @DisplayName("모임 게시글 작성")
    void CreateMoimPost() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/search/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("address", "서울시")
                        .header("Authorization", hostAuthToken))
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        log.info("서울특별시 : {}", contentAsString);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        /*
        * {
              "items": [
                {
                  "title": "서울특별시청",
                  "address": "서울특별시 중구 태평로1가 31 서울특별시청",
                  "roadAddress": "서울특별시 중구 세종대로 110 서울특별시청",
                  "mapx": 126.9783882,
                  "mapy": 37.5666103
                },
                ...
              ]
            }
        * items 안에 5개 검색 기록이 있음.
        * */
        JsonNode rootNode = objectMapper.readTree(contentAsString);
        JsonNode itemArray = rootNode.get("items");
        JsonNode firstItem = itemArray.get(0);

        title = firstItem.get("title").asText();
        address = firstItem.get("address").asText();
        roadAddress = firstItem.get("roadAddress").asText();
        mapx = firstItem.get("mapx").asDouble();
        mapy = firstItem.get("mapy").asDouble();
        ;

        // MoimPostRequestDTO 생성
        moimPostRequestDTO = MoimPostRequestDTO.builder()
                .memberId(hostMember.getMemberId())
                .title("제목")
                .category("예술")
                .content("내용")
                .location(title)
                .address(address)
                .roadAddress(roadAddress)
                .mapx(mapx)
                .mapy(mapy)
                .maxParticipants(5)
                .moimDate(LocalDateTime.parse("2025-03-01T14:30:00"))
                .build();

        mockMvc.perform(post("/api/moim-post/write")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(moimPostRequestDTO))
                        .header("Authorization", hostAuthToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("message").value("모임 게시글 작성이 완료되었습니다."));


        //DB에 생성 되었는지 검증
        List<MoimPost> all = moimPostRepository.findAll();
        assertThat(all).isNotNull();

        postId = all.get(0).getMoimPostId();
    }

    @Test
    @Order(4)
    @DisplayName("(host) 신청 받은 모임 리스트 조회, 빈 리스트 반환")
    void receivedParticipantsListisEmpty() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        MvcResult mvcResult = mockMvc.perform(get("/api/participation/received-participation/{ownerId}",
                        hostMember.getMemberId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", hostAuthToken))
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();

        log.info("contentAsString : {} ", contentAsString);

        List<MoimParticipationListResponseDTO> participationList = objectMapper.readValue(contentAsString,
                new TypeReference<List<MoimParticipationListResponseDTO>>() {
                });

        assertThat(participationList.size()).isZero();
    }

    @Test
    @Order(5)
    @DisplayName("(participant) 신청한 목록 조회, 빈 리스트 반환")
    void getMyParticipationListIsEmpty() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/api/participation/my-participation/{memberId}", participantMember.getMemberId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", participantAuthToken))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();

        log.info("contentAsString : {}", contentAsString);

        ObjectMapper objectMapper = new ObjectMapper();

        List<MoimParticipationListResponseDTO> participationList = objectMapper.readValue(contentAsString,
                new TypeReference<List<MoimParticipationListResponseDTO>>() {
                });

        assertThat(participationList.size()).isZero();

    }



    @Test
    @Order(6)
    @DisplayName("(participant) 모임 신청")
    void applyForMoim() throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();

        moimParticipationRequestDTO.setIntro("참여 신청 소개");
        moimParticipationRequestDTO.setReasonParticipation("참여 신청 이유");

        mockMvc.perform(post("/api/participation/apply")
                        .param("moimPostId", String.valueOf(postId))
                        .param("memberId", String.valueOf(participantMember.getMemberId()))
                        .content(objectMapper.writeValueAsString(moimParticipationRequestDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", participantAuthToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("message").value("참여 신청이 완료되었습니다."));

        List<MoimParticipation> getParticipantList = moimParticipationRepository.findByMember(participantMember);
        assertThat(getParticipantList.size()).isOne();


    }

    @Test
    @Order(7)
    @DisplayName("(participant) 모임 신청 Exception -> '이미 신청한 모임입니다'")
    void applyForMoimBadRequest() throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();

        moimParticipationRequestDTO.setIntro("참여 신청 소개");
        moimParticipationRequestDTO.setReasonParticipation("참여 신청 이유");

        mockMvc.perform(post("/api/participation/apply")
                        .param("moimPostId", String.valueOf(postId))
                        .param("memberId", String.valueOf(participantMember.getMemberId()))
                        .content(objectMapper.writeValueAsString(moimParticipationRequestDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", participantAuthToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("이미 신청한 모임입니다."));

    }

    @Test
    @Order(8)
    @DisplayName("(host) 신청 받은 모임 리스트 조회, 한건 조회")
    void receivedParticipantsList() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        MvcResult mvcResult = mockMvc.perform(get("/api/participation/received-participation/{ownerId}",
                        hostMember.getMemberId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", hostAuthToken))
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();

        List<MoimParticipationListResponseDTO> participationList = objectMapper.readValue(contentAsString,
                new TypeReference<List<MoimParticipationListResponseDTO>>() {
        });

        assertThat(participationList.size()).isOne();
        participationId = participationList.get(0).getMoimParticipationRequestId();
    }

    @Test
    @Order(9)
    @DisplayName("(host) 신청 받은 모임 단건 조회")
    void receivedParticipant() throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();

        moimParticipationRequestDTO.setIntro("참여 신청 소개");
        moimParticipationRequestDTO.setReasonParticipation("참여 신청 이유");

        MvcResult mvcResult = mockMvc.perform(get("/api/participation/received/{participationId}", participationId)
                        .param("ownerId", String.valueOf(hostMember.getMemberId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", hostAuthToken))
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();

        log.info(contentAsString);
        JsonNode jsonNode = objectMapper.readTree(contentAsString);
        String intro = jsonNode.get("intro").asText();
        String reasonParticipation = jsonNode.get("reasonParticipation").asText();
        String participationStatus = jsonNode.get("participationStatus").asText();

        assertThat(intro).isEqualTo("참여 신청 소개");
        assertThat(reasonParticipation).isEqualTo("참여 신청 이유");
        assertThat(participationStatus).isEqualTo(ParticipationStatus.PENDING.getLabel());
    }

    @Test
    @Order(10)
    @DisplayName("(participant) 신청한 목록 조회, 한건 조회")
    void getMyParticipationList() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/api/participation/my-participation/{memberId}", participantMember.getMemberId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", participantAuthToken))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();

        log.info("contentAsString : {}", contentAsString);

        ObjectMapper objectMapper = new ObjectMapper();

        List<MoimParticipationListResponseDTO> participationList = objectMapper.readValue(contentAsString,
                new TypeReference<List<MoimParticipationListResponseDTO>>() {
                });

        assertThat(participationList.size()).isOne();

    }

    @Test
    @Order(11)
    @DisplayName("(host) 특정 모임에 수락한 목록 조회, 빈 리스트 반환")
    void getAcceptParticipationIsEmpty() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/api/participation/accepted-participants/{moimPostId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", participantAuthToken))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();

        String contentAsString = mvcResult.getResponse().getContentAsString();

        List<MoimParticipationListResponseDTO> participationList = objectMapper.readValue(contentAsString,
                new TypeReference<List<MoimParticipationListResponseDTO>>() {
                });

        assertThat(participationList.size()).isZero();

    }

    @Test
    @Order(12)
    @DisplayName("(host) 특정 모임에 거절한 목록 조회, 빈 리스트 반환")
    void getRejectedParticipationIsEmpty() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/api/participation/rejected-participants/{moimPostId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", participantAuthToken))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();

        String contentAsString = mvcResult.getResponse().getContentAsString();

        List<MoimParticipationListResponseDTO> participationList = objectMapper.readValue(contentAsString,
                new TypeReference<List<MoimParticipationListResponseDTO>>() {
                });

        assertThat(participationList.size()).isZero();

    }


    @Test
    @Order(13)
    @DisplayName("(host) 신청 수락")
    void acceptParticipation() throws Exception {

        mockMvc.perform(post("/api/participation/accept/{participationId}", participationId)
                .param("ownerId", String.valueOf(hostMember.getMemberId()))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", participantAuthToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("참여 신청이 수락되었습니다."));
    }

    @Test
    @Order(14)
    @DisplayName("(host) 특정 모임에 수락한 목록 조회, 1개 반환")
    void getAcceptParticipation() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/api/participation/accepted-participants/{moimPostId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", participantAuthToken))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();

        String contentAsString = mvcResult.getResponse().getContentAsString();

        List<MoimParticipationListResponseDTO> participationList = objectMapper.readValue(contentAsString,
                new TypeReference<List<MoimParticipationListResponseDTO>>() {
                });

        assertThat(participationList.size()).isOne();

    }


}
