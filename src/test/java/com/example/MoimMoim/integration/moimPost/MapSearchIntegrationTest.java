package com.example.MoimMoim.integration.moimPost;

import com.example.MoimMoim.domain.Member;
import com.example.MoimMoim.repository.MemberRepository;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MapSearchIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(MapSearchIntegrationTest.class);
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    String email, password;
    String authToken;
    Member member;

    @BeforeAll
    void setup() {
        email = "test@example.com";
        password = "Aa!12345";
    }

    @AfterAll
    void cleanup() {
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
    @DisplayName("주소 검색")
    void mapSearch() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/search/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("address", "서울시")
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        log.info("서울특별시 : {}", contentAsString);

    }

}
