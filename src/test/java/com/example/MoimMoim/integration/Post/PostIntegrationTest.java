package com.example.MoimMoim.integration.Post;


import com.example.MoimMoim.domain.Member;
import com.example.MoimMoim.domain.Post;
import com.example.MoimMoim.dto.post.PostRequestDTO;
import com.example.MoimMoim.dto.post.PostResponseDTO;
import com.example.MoimMoim.dto.post.PostSummaryResponseDTO;
import com.example.MoimMoim.enums.Category;
import com.example.MoimMoim.repository.MemberRepository;
import com.example.MoimMoim.repository.PostRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
* 인메모리 DB를 사용하기 때문에 RollBack은 필요없다.
*
*
* default : TestInstance.Lifecycle.PER_METHOD)
* PER_CLASS
*  - 각 테스트 메서드가 실행될 떄마다 새로운 테스트 클래스 인스턴스를 만들지 않고 하나의 인스턴스를 공유
*  - email, passowrd 등 인스턴트 들이 초기화되지 않음
* */
@TestInstance(TestInstance.Lifecycle.PER_CLASS) //
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Order 테스트 순서를 보장
public class PostIntegrationTest {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(PostIntegrationTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PostRepository postRepository;

    String email, password;
    String authToken;
    Member member;
    Long postId;

    @BeforeAll
    void setup() {
        email = "test@example.com";
        password = "Aa!12345";
    }

    @AfterAll
    void cleanup() {
        postRepository.deleteAll();
        memberRepository.deleteAll();
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
                    "gender": "MALE",
                    "nickname": "길동이",
                    "birthday": "1995-08-15"
                }
                """.formatted(email, password);

        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupRequestBody))
                .andExpect(status().isCreated())
                .andExpect(content().string("회원가입이 완료되었습니다."));

        // Ensure the member is saved
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

        // Get the authentication token
        authToken = loginResult.getResponse().getHeader("Authorization");
        assertThat(authToken).isNotNull();
    }

    @Test
    @Order(3)
    @DisplayName("게시글 작성")
    void testPostWrite() throws Exception {
        String postRequestBody = """
                {
                    "title" : "게시글 제목",
                    "category" : "ART",
                    "content" : "게시글 내용",
                    "memberId" : "%d"
                }
                """.formatted(member.getMemberId());

        mockMvc.perform(post("/api/post/write")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postRequestBody)
                        .header("Authorization", authToken))
                .andExpect(status().isCreated())
                .andExpect(content().string("게시글 작성이 완료되었습니다."));
    }

    @Test
    @Order(4)
    @DisplayName("게시글 목록 조회")
    void testPostList() throws Exception {
        MvcResult postListResult = mockMvc.perform(get("/api/post/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andReturn();

        String responsePostList = postListResult.getResponse().getContentAsString();
        log.info("Response Body: {}", responsePostList);

        ObjectMapper objectMapper = new ObjectMapper();
        List<PostSummaryResponseDTO> postList = objectMapper.readValue(responsePostList, new TypeReference<List<PostSummaryResponseDTO>>() {});

        // Extract the first post
        PostSummaryResponseDTO firstPost = postList.get(0);
        postId = firstPost.getPostId();
        assertThat(firstPost).isNotNull();
    }

    @Test
    @Order(5)
    @DisplayName("게시글 단건 조회")
    void testPostDetail() throws Exception {
        MvcResult postResult = mockMvc.perform(get("/api/post/post-id/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andReturn();

        String responsePost = postResult.getResponse().getContentAsString();
        log.info("Response Body: {}", responsePost);

        ObjectMapper objectMapper = new ObjectMapper();
        PostResponseDTO postResponseDTO = objectMapper.readValue(responsePost, PostResponseDTO.class);
        assertThat(postResponseDTO.getPostId()).isEqualTo(postId);
    }




    @Test
    @Order(6)
    @DisplayName("게시글 수정")
    void testPostEdit() throws Exception {
        PostRequestDTO postRequestDTO = PostRequestDTO.builder()
                .title("수정된 제목")
                .category("ART")
                .content("수정된 내용")
                .memberId(member.getMemberId())
                .build();

        mockMvc.perform(put("/api/post/post-id/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authToken)
                        .content(new ObjectMapper().writeValueAsString(postRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("게시글 수정이 완료되었습니다."));
    }

    @Test
    @Order(7)
    @DisplayName("수정된 게시글 확인")
    void testPostUpdated() throws Exception {
        MvcResult updatedPostResult = mockMvc.perform(get("/api/post/post-id/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andReturn();

        String updatedPostResponse = updatedPostResult.getResponse().getContentAsString();
        log.info("Updated Post Response: {}", updatedPostResponse);

        PostResponseDTO updatedPostResponseDTO = new ObjectMapper().readValue(updatedPostResponse, PostResponseDTO.class);
        assertThat(updatedPostResponseDTO.getTitle()).isEqualTo("수정된 제목");
        assertThat(updatedPostResponseDTO.getContent()).isEqualTo("수정된 내용");
    }

    @Test
    @Order(8)
    @DisplayName("게시글 삭제")
    void testPostDelete() throws Exception {
        mockMvc.perform(delete("/api/post/post-id/{postId}", postId)
                        .param("memberId", String.valueOf(member.getMemberId()))
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(content().string("게시글 삭제가 완료되었습니다."));

        Optional<Post> deletedPost = postRepository.findById(postId);
        assertThat(deletedPost).isEmpty();
    }
}
