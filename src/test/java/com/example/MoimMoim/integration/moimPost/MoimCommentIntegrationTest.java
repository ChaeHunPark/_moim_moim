package com.example.MoimMoim.integration.moimPost;

import com.example.MoimMoim.domain.Member;
import com.example.MoimMoim.domain.MoimPost;
import com.example.MoimMoim.dto.moimPost.MoimCommentRequestDTO;
import com.example.MoimMoim.dto.moimPost.MoimCommentResponseDTO;
import com.example.MoimMoim.dto.moimPost.MoimPostRequestDTO;
import com.example.MoimMoim.enums.Category;
import com.example.MoimMoim.repository.MemberRepository;
import com.example.MoimMoim.repository.MoimCommentRepository;
import com.example.MoimMoim.repository.MoimPostRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.assertj.core.api.Assertions;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MoimCommentIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(MapSearchIntegrationTest.class);
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MoimPostRepository moimPostRepository;

    @Autowired
    private MoimCommentRepository moimCommentRepository;

    // 회원가입 변수
    String email, password;
    String authToken;

    // 멤버 객체
    Member member;

    // 모임 게시글 작성 DTO
    MoimPostRequestDTO moimPostRequestDTO;
    // DTO의 주소에 사용될 변수들
    private String title;
    private String address;
    private String roadAddress;
    private double mapx;
    private double mapy;

    // 모임 게시글 id
    long firstItemPostId;


    // Comment
    MoimCommentRequestDTO moimCommentRequestDTO = new MoimCommentRequestDTO();
    Long commentId;

    @BeforeAll
    void setup() {
        email = "test@example.com";
        password = "Aa!12345";
    }

    @Test
    @Order(1)
    @DisplayName("회원가입")
    void testSignup() throws Exception {

        String signupRequestBody = """
                {
                    "email": "%s",
                    "password": "%s",
                    "phone": "010-1234-5678",
                    "name": "홍길동",
                    "gender": "남자",
                    "nickname": "길동이",
                    "birthday": "1995-08-15"
                }
                """.formatted(email, password);

        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupRequestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("message").value("회원가입이 완료되었습니다."));

        // member가 잘 저장 되었는가 확인
        member = memberRepository.findByEmail(email).orElseThrow();
        assertThat(member).isNotNull();
    }

    @Test
    @Order(2)
    @DisplayName("로그인")
    void testLogin() throws Exception {
        String loginRequestBody = """
                {
                    "username": "%s",
                    "password": "%s"
                }
                """.formatted(email, password);

        MvcResult loginResult = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestBody))
                .andExpect(status().isOk())
                .andReturn();

        // 토큰이 잘 저장 되었는가 확인
        authToken = loginResult.getResponse().getHeader("Authorization");
        assertThat(authToken).isNotNull();
    }


    @Test
    @Order(3)
    @DisplayName("모임 게시글 작성")
    void CreateMoimPost() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/search/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("address", "서울시")
                        .header("Authorization", authToken))
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
                .memberId(member.getMemberId())
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
                        .header("Authorization", authToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("message").value("모임 게시글 작성이 완료되었습니다."));


        //DB에 생성 되었는지 검증
        List<MoimPost> all = moimPostRepository.findAll();
        Assertions.assertThat(all).isNotNull();
    }

    @Test
    @Order(4)
    @DisplayName("모임 게시글 목록 조회")
    void ReadMoimPosts() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/moim-post/moim-posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andReturn();
        ObjectMapper objectMapper = new ObjectMapper();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        log.info("contentAsString1 : {}", contentAsString);
        JsonNode rootNode = objectMapper.readTree(contentAsString).get("content").get(0);
        firstItemPostId = rootNode.get("postId").longValue();


        Optional<MoimPost> getPost = moimPostRepository.findById(firstItemPostId);
        assertThat(getPost).isNotNull();


    }


    @Test
    @Order(5)
    @DisplayName("댓글 생성")
    void MoimPostCreateComment() throws Exception {
        moimCommentRequestDTO.setPostId(firstItemPostId);
        moimCommentRequestDTO.setMemberId(member.getMemberId());
        moimCommentRequestDTO.setContent("댓글 내용");

        ObjectMapper objectMapper = new ObjectMapper();


        mockMvc.perform(post("/api/moim-comment/write")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authToken)
                        .content(objectMapper.writeValueAsString(moimCommentRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("message").value("댓글 작성이 완료되었습니다."));

    }

    @Test
    @Order(6)
    @DisplayName("모임 게시글 댓글 확인")
    void ReadMoimPostComment() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/moim-post/moim-post-id/{moimPostId}", firstItemPostId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(contentAsString);

        // 댓글 리스트
        List<MoimCommentResponseDTO> commentList = objectMapper.readValue(jsonNode.get("moimCommentList").toString(),
                new TypeReference<List<MoimCommentResponseDTO>>() {
                });
        // 댓글 내용
        String content = commentList.get(0).getContent();

        commentId = commentList.get(0).getCommentId();

        assertThat(commentList.size()).isEqualTo(1);
        assertThat(content).isEqualTo("댓글 내용");
    }

    @Test
    @Order(7)
    @DisplayName("모임 게시글 댓글 수정")
    void updateMoimPostComment() throws Exception {
        moimCommentRequestDTO.setContent("수정된 댓글 내용");

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(put("/api/moim-comment/moim-comment-id/{moimCommentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authToken)
                        .content(objectMapper.writeValueAsString(moimCommentRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("댓글이 성공적으로 수정되었습니다."));
    }

    @Test
    @Order(8)
    @DisplayName("모임 게시글 수정된 댓글 확인")
    void moimPostUpdatedComment() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/moim-post/moim-post-id/{moimPostId}", firstItemPostId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        log.info(contentAsString);
        JsonNode jsonNode = objectMapper.readTree(contentAsString);

        // 댓글 리스트
        List<MoimCommentResponseDTO> commentList = objectMapper.readValue(jsonNode.get("moimCommentList").toString(),
                new TypeReference<List<MoimCommentResponseDTO>>() {
                });
        // 댓글 내용
        String content = commentList.get(0).getContent();

        assertThat(content).isEqualTo("수정된 댓글 내용");
    }

    @Test
    @Order(9)
    @DisplayName("모임 게시글 댓글 삭제")
    void deleteMoimPostComment() throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(delete("/api/moim-comment/moim-comment-id/{moimCommentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authToken)
                        .content(objectMapper.writeValueAsString(moimCommentRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("댓글 삭제가 완료되었습니다."));
    }

    @Test
    @Order(10)
    @DisplayName("모임 게시글 삭제된 댓글 확인")
    void MoimPostDeletedComment() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/moim-post/moim-post-id/{moimPostId}", firstItemPostId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(contentAsString);

        // 댓글 리스트
        List<MoimCommentResponseDTO> commentList = objectMapper.readValue(jsonNode.get("moimCommentList").toString(),
                new TypeReference<List<MoimCommentResponseDTO>>() {
                });


        assertThat(commentList.size()).isEqualTo(0);


    }
}