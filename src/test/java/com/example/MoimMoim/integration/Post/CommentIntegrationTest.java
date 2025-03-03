package com.example.MoimMoim.integration.Post;

import com.example.MoimMoim.domain.Member;
import com.example.MoimMoim.dto.post.PostResponseDTO;
import com.example.MoimMoim.repository.MemberRepository;
import com.example.MoimMoim.repository.PostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.mapping.Map;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CommentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PostRepository postRepository;

    private String email, password, authToken;
    private Member member;
    private Long postId, commentId;

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
                    "gender": "남자",
                    "nickname": "길동이",
                    "birthday": "1995-08-15"
                }
                """.formatted(email, password);

        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupRequestBody))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"message\":\"회원가입이 완료되었습니다.\"}"));

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
                    "category" : "예술",
                    "content" : "게시글 내용",
                    "memberId" : "%d"
                }
                """.formatted(member.getMemberId());

        mockMvc.perform(post("/api/post/write")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postRequestBody)
                        .header("Authorization", authToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("게시글 작성이 완료되었습니다."));

        // Fetch the postId for subsequent comment tests
        postId = postRepository.findAll().get(0).getPostId();
    }

    @Test
    @Order(4)
    @DisplayName("게시글 댓글 추가")
    void testComment() throws Exception {
        String commentRequestBody = """
                {
                    "content" : "댓글 내용",
                    "memberId" : "%d",
                    "postId" : "%d"
                }
                """.formatted(member.getMemberId(), postId);

        mockMvc.perform(post("/api/comment/write")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authToken)
                        .content(commentRequestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("댓글 작성이 완료되었습니다."));
    }

    @Test
    @Order(5)
    @DisplayName("게시글의 댓글 조회")
    void testPostDetailComment() throws Exception {
        MvcResult postResult = mockMvc.perform(get("/api/post/post-id/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andReturn();

        String responsePost = postResult.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        PostResponseDTO postResponseDTO = objectMapper.readValue(responsePost, PostResponseDTO.class);

        commentId = postResponseDTO.getCommentList().get(0).getCommentId();
        assertThat(postResponseDTO.getCommentList().size()).isEqualTo(1); // Check if one comment exists
        assertThat(postResponseDTO.getCommentList().get(0).getContent()).isEqualTo("댓글 내용");
    }

    @Test
    @Order(6)
    @DisplayName("댓글 수정")
    void testCommentEdit() throws Exception {
        String commentRequestBody = """
                {
                    "content" : "수정된 댓글",
                    "memberId" : "%d",
                    "postId" : "%d"
                }
                """.formatted(member.getMemberId(), postId);

        mockMvc.perform(put("/api/comment/comment-id/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authToken)
                        .content(commentRequestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("댓글이 수정이 완료되었습니다."));
    }

    @Test
    @Order(7)
    @DisplayName("게시글의 수정된 댓글 조회")
    void testPostDetailUpdateComment() throws Exception {
        MvcResult postResult = mockMvc.perform(get("/api/post/post-id/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andReturn();

        String responsePost = postResult.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        PostResponseDTO postResponseDTO = objectMapper.readValue(responsePost, PostResponseDTO.class);

        assertThat(postResponseDTO.getCommentList().get(0).getContent()).isEqualTo("수정된 댓글");
    }

    @Test
    @Order(8)
    @DisplayName("댓글 삭제")
    void testCommentDelete() throws Exception {

        String postRequestBody = """
                {
                    "content" : "수정된 댓글",
                    "memberId" : "%d",
                    "postId" : "%d"
                }
                """.formatted(member.getMemberId(), postId);


        mockMvc.perform(delete("/api/comment/comment-id/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authToken)
                        .content(postRequestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("댓글 삭제가 완료되었습니다."));
    }

    @Test
    @Order(9)
    @DisplayName("게시글의 삭제된 댓글 조회")
    void testPostDetailDeleteComment() throws Exception {
        MvcResult postResult = mockMvc.perform(get("/api/post/post-id/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andReturn();

        String responsePost = postResult.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        PostResponseDTO postResponseDTO = objectMapper.readValue(responsePost, PostResponseDTO.class);

        assertThat(postResponseDTO.getCommentList().size()).isZero(); // Check if no comments exist
    }
}