package com.example.MoimMoim.dto.member;

import com.example.MoimMoim.enums.Gender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MemberSignUpRequestDTOTest {
    // Validator 초기화
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();


    @Test
    @DisplayName("DTO -> JSON 변환 테스트")
    void testDtoToJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        MemberSignUpRequestDTO dto = new MemberSignUpRequestDTO(
                "valid@example.com",      // 이메일
                "ValidPassword1@",        // 비밀번호
                "010-1234-5678",          // 전화번호
                "John Doe",               // 이름
                "남자",              // 성별 (예시 Gender enum: MALE, FEMALE 등)
                "johnny",                 // 닉네임
                "1990-01-01"              // 생년월일
        );

        //
        String json = objectMapper.writeValueAsString(dto);

        Assertions.assertThat(json)
                .isEqualTo(
                        "{" +
                                "\"email\":\"valid@example.com\"," +
                                "\"password\":\"ValidPassword1@\"," +
                                "\"phone\":\"010-1234-5678\"," +
                                "\"name\":\"John Doe\"," +
                                "\"gender\":\"남자\"," +
                                "\"nickname\":\"johnny\"," +
                                "\"birthday\":\"1990-01-01\"" +
                                "}"
                );



    }

    @Test
    @DisplayName("JSON -> DTO 변환 테스트")
    void testJsonToDto() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        // JSON 문자열 정의
        String json = "{"
                + "\"email\":\"valid@example.com\","
                + "\"password\":\"ValidPassword1@\","
                + "\"phone\":\"010-1234-5678\","
                + "\"name\":\"John Doe\","
                + "\"gender\":\"남자\","
                + "\"nickname\":\"johnny\","
                + "\"birthday\":\"1990-01-01\""
                + "}";

        // JSON -> DTO 변환 (역직렬화)
        MemberSignUpRequestDTO dto = objectMapper.readValue(json, MemberSignUpRequestDTO.class);

        // 변환된 DTO의 값 검증
        Assertions.assertThat(dto.getEmail()).isEqualTo("valid@example.com");
        Assertions.assertThat(dto.getPassword()).isEqualTo("ValidPassword1@");
        Assertions.assertThat(dto.getPhone()).isEqualTo("010-1234-5678");
        Assertions.assertThat(dto.getName()).isEqualTo("John Doe");
        Assertions.assertThat(dto.getGender()).isEqualTo("남자");
        Assertions.assertThat(dto.getNickname()).isEqualTo("johnny");
        Assertions.assertThat(dto.getBirthday()).isEqualTo("1990-01-01");
    }


    @Test
    @DisplayName("회원가입, 유효성 검사를 실패하지 않아야 한다.")
    void validDTOTest() {
        // 유효한 데이터로 DTO 생성
        MemberSignUpRequestDTO dto = new MemberSignUpRequestDTO(
                "valid@example.com",      // 이메일
                "ValidPassword1@",        // 비밀번호
                "010-1234-5678",          // 전화번호
                "John Doe",               // 이름
                "예술",              // 성별 (예시 Gender enum: MALE, FEMALE 등)
                "johnny",                 // 닉네임
                "1990-01-01"              // 생년월일
        );

        // 유효성 검사
        Set<ConstraintViolation<MemberSignUpRequestDTO>> violations = validator.validate(dto);

        // 유효성 검사가 실패한 항목이 없으면 테스트 통과
        assertTrue(violations.isEmpty(), "유효성 검사 실패: " + violations);
    }

    @Test
    @DisplayName("회원가입, 잘못된 데이터 입력시 에러 메시지를 반환한다.")
    void invalidDTOTest() {
        // 잘못된 데이터로 DTO 생성
        MemberSignUpRequestDTO dto = new MemberSignUpRequestDTO(
                "invalid-email",          // 잘못된 이메일
                "short",                  // 너무 짧은 비밀번호
                "invalid-phone",          // 잘못된 전화번호 형식
                null,                       // 비어 있는 이름
                null,                     // 성별이 null (필수 입력값)
                "a",                      // 너무 짧은 닉네임
                "1990-13-01"              // 잘못된 생년월일 형식 (13월)
        );

        // 유효성 검사
        Set<ConstraintViolation<MemberSignUpRequestDTO>> violations = validator.validate(dto);

        // 각 에러 메시지를 검증
        for (ConstraintViolation<MemberSignUpRequestDTO> violation : violations) {
            if ("email".equals(violation.getPropertyPath().toString())) {
                assertEquals("이메일 형식이 올바르지 않습니다.", violation.getMessage());
            }
            if ("password".equals(violation.getPropertyPath().toString())) {
                assertEquals("비밀번호는 8자 이상이어야 하며, 소문자, 대문자, 특수문자를 포함해야 합니다.", violation.getMessage());
            }
            if ("phone".equals(violation.getPropertyPath().toString())) {
                assertEquals("전화번호 형식이 올바르지 않습니다.", violation.getMessage());
            }
            if ("name".equals(violation.getPropertyPath().toString())) {
                assertEquals("이름은 필수 입력 값입니다.", violation.getMessage());
            }
            if ("gender".equals(violation.getPropertyPath().toString())) {
                assertEquals("성별은 필수 입력 값입니다.", violation.getMessage());
            }
            if ("nickname".equals(violation.getPropertyPath().toString())) {
                assertEquals("닉네임은 2자 이상이어야 합니다.", violation.getMessage());
            }
            if ("birthday".equals(violation.getPropertyPath().toString())) {
                assertEquals("생년월일은 YYYY-MM-DD 형식이어야 합니다.", violation.getMessage());
            }
        }
    }

}