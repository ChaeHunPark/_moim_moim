package com.example.MoimMoim.service;

import com.example.MoimMoim.domain.Member;
import com.example.MoimMoim.domain.Role;
import com.example.MoimMoim.dto.member.MemberSignUpRequestDTO;
import com.example.MoimMoim.enums.Gender;
import com.example.MoimMoim.enums.RoleName;
import com.example.MoimMoim.exception.member.DuplicateNicknameException;
import com.example.MoimMoim.exception.member.EmailAlreadyExistsException;
import com.example.MoimMoim.exception.member.RoleNotFoundException;
import com.example.MoimMoim.repository.MemberRepository;
import com.example.MoimMoim.repository.RoleRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static reactor.core.publisher.Mono.when;


@ExtendWith(MockitoExtension.class) //Mockito의 기능을 활성화
class MemberSignupServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock // 가짜 객체를 삽입, 사용할 메서드의 반환값은 개발자가 정한다.
    private PasswordEncoder passwordEncoder;

    @InjectMocks // 필요한 의존성을(위 Mock 들을)자동으로 주입, 실제 우리가 테스트할 부분
    private MemberSignupServiceImpl memberSignupService; // 실제객체를 주입해야한다.

    MemberSignUpRequestDTO signUpRequestDTO = new MemberSignUpRequestDTO();

    @BeforeEach
    void setup(){
        signUpRequestDTO.setEmail("email@naver.com");
        signUpRequestDTO.setPassword("Password@@1");
        signUpRequestDTO.setPhone("010-2222-2222");
        signUpRequestDTO.setName("name");
        signUpRequestDTO.setGender(Gender.MALE);
        signUpRequestDTO.setNickname("nickname");
        signUpRequestDTO.setBirthday("1991-01-01");

    }


    @Test
    @DisplayName("회원가입 서비스 성공 테스트")
    public void testSignup_Success() {
        // given
        // 비밀번호 암호화 Mock
        Mockito.when(passwordEncoder.encode(signUpRequestDTO.getPassword())).thenReturn("encodedPassword");

        // 역할 검증 Mock
        Role mockRole = new Role(1L, RoleName.ROLE_USER);
        Mockito.when(roleRepository.findByRoleName(RoleName.ROLE_USER)).thenReturn(Optional.of(mockRole));

//         이메일이 존재하지 않으면 정상 회원가입 처리
        Mockito.when(memberRepository.existsByEmail(signUpRequestDTO.getEmail())).thenReturn(false);
//         닉네임이 존재하지 않으면 정상 회원가입 처리
        Mockito.when(memberRepository.existsByNickname(signUpRequestDTO.getNickname())).thenReturn(false);

        // when
        memberSignupService.signup(signUpRequestDTO);

        // then
        // memberRepository.save가 한 번 호출되었는지 검증
        Mockito.verify(memberRepository, Mockito.times(1)).save(any(Member.class));
    }


    @Test
    @DisplayName("이메일이 이미 존재하는 경우")
    public void testSignup_EmailAlreadyExists() {
        // given

        // 이미 이메일이 존재하는 경우
        Mockito.when(memberRepository.existsByEmail(signUpRequestDTO.getEmail())).thenReturn(true);

        // when / then
        assertThatThrownBy(() ->
                memberSignupService.signup(signUpRequestDTO)
        ).isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage("이메일이 이미 존재합니다.");
    }

    @Test
    @DisplayName("닉네임이 이미 존재하는 경우")
    public void testSignup_NicknameAlreadyExists() {
        // given

        // 이미 이메일이 존재하는 경우
        Mockito.when(memberRepository.existsByNickname(signUpRequestDTO.getNickname())).thenReturn(true);

        assertThatThrownBy(() ->
                memberSignupService.signup(signUpRequestDTO)
        ).isInstanceOf(DuplicateNicknameException.class)
                .hasMessage("이미 사용 중인 닉네임입니다.");
    }

    @Test
    @DisplayName("권한 검증 성공 테스트")
    public void testValidateRole_Success() {
        // given
        RoleName roleName = RoleName.ROLE_USER;
        Role mockRole = new Role(1L, roleName);

        // when
        Mockito.when(roleRepository.findByRoleName(roleName)).thenReturn(Optional.of(mockRole));

        Role role = memberSignupService.validateRole(roleName);

        // then
        Assertions.assertNotNull(role);
        Assertions.assertEquals(roleName, role.getRoleName());
    }

    @Test
    @DisplayName("권한 검증 오류 테스트")
    public void testValidateRole_RoleNotFound() {
        // given
        RoleName roleName = RoleName.ROLE_USER;

        // when / then
        Mockito.when(roleRepository.findByRoleName(roleName)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                memberSignupService.signup(signUpRequestDTO)
        ).isInstanceOf(RoleNotFoundException.class)
                .hasMessage("해당 권한은 존재하지 않습니다.");

    }



}