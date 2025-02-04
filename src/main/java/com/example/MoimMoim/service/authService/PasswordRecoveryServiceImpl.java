package com.example.MoimMoim.service.authService;

import com.example.MoimMoim.domain.Member;
import com.example.MoimMoim.dto.passwordrecovery.AccountVerificationRequestDTO;
import com.example.MoimMoim.dto.passwordrecovery.CodeVerificationRequestDTO;
import com.example.MoimMoim.dto.passwordrecovery.PasswordResetRequestDTO;
import com.example.MoimMoim.dto.passwordrecovery.RecoveryMethodRequestDTO;
import com.example.MoimMoim.exception.auth.InvalidAuthenticationMethodException;
import com.example.MoimMoim.exception.member.MemberNotFoundException;
import com.example.MoimMoim.exception.auth.TooManyRequestsException;
import com.example.MoimMoim.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;


// 인증 이메일 보내기

@Service
public class PasswordRecoveryServiceImpl implements PasswordRecoveryService{


    private final JavaMailSender mailSender;
    private final MemberRepository memberRepository;
    private final VerificationCodeManager verificationCodeManager;
    private final PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(PasswordRecoveryServiceImpl.class);

    @Autowired
    public PasswordRecoveryServiceImpl(JavaMailSender mailSender, MemberRepository memberRepository, VerificationCodeManager verificationCodeManager, PasswordEncoder passwordEncoder) {
        this.mailSender = mailSender;
        this.memberRepository = memberRepository;
        this.verificationCodeManager = verificationCodeManager;
        this.passwordEncoder = passwordEncoder;
    }

    // 6자리 인증번호 생성 keep
    public String generateVerificationCode() {
        Random random = new Random();
        int number = random.nextInt(999999);
        return String.format("%06d", number);
    }

    // 이메일로 인증번호 전송
    public void sendEmail(String email) {

        logger.info("메소드 진입");
        if (!verificationCodeManager.canRequestNewCode(email)) { //keep
            throw new TooManyRequestsException("인증번호를 너무 자주 요청하셨습니다. 잠시 후 다시 시도해주세요.");
        }

        String verificationCode = generateVerificationCode(); // 6자리 인증번호 생성
        logger.info("인증번호 생성");


        //인증번호를 CHashMap에 저장
        verificationCodeManager.saveVerificationCode(email, verificationCode); //keep
        logger.info("해쉬맵에 저장");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("모임모임 비밀번호 찾기 인증번호");
        message.setFrom("moim_moim@moim.com");

        String body = "안녕하세요, 모임모임입니다. \n\n" +
                "귀하의 인증번호는 다음과 같습니다:\n\n" +
                "인증번호: " + verificationCode + "\n\n" +
                "해당 인증번호를 입력하여 인증을 완료해 주세요.";
        message.setText(body);

        mailSender.send(message);
    }



    // 멤버가 존재하는지 리턴.
    @Override
    public void isAccountExists(AccountVerificationRequestDTO accountVerificationRequestDTO) {
        //직관성을 위해 boolean 변수를 따로 선언
        boolean exists = memberRepository.existsByNameAndEmail(accountVerificationRequestDTO.getName(),
                accountVerificationRequestDTO.getEmail());

        //멤버가 존재하지 않으면 예외를 던진다.
        if(!exists){
            throw new MemberNotFoundException("회원정보가 일치하지 않습니다.");
        }
    }



    // 이메일 또는 카카오톡으로 인증번호 6자리 보내기
    // 인증방식 추가예정을 위해 select 인증.
    // 인증 방식 선택 및 코드 전송
    @Override
    public void selectRecoveryMethodAndSendCode(RecoveryMethodRequestDTO recoveryMethodRequestDTO) {

        boolean exists = memberRepository.existsByEmail(recoveryMethodRequestDTO.getEmail());

        if(!exists){
            throw new MemberNotFoundException("회원정보를 찾을 수 없습니다.");
        }


        if (recoveryMethodRequestDTO.getMethod().equalsIgnoreCase("email")) {
            sendEmail(recoveryMethodRequestDTO.getEmail());
        } else {
            throw new InvalidAuthenticationMethodException("잘못된 인증방식 입니다.");
        }
    }


    // 인증번호 유효성 검사
    @Override
    public void verifyCode(CodeVerificationRequestDTO codeVerificationRequestDTO) {
        verificationCodeManager.validateVerificationCode(codeVerificationRequestDTO.getEmail(),
                codeVerificationRequestDTO.getUserInputCode());
    }


    /*
    * 인스턴스 중 이메일 정보가 변경될 가능성이 있을지도?
    * */
    @Transactional
    @Override
    public void resetPassword(PasswordResetRequestDTO passwordResetRequestDTO) {
        logger.info("멤버찾기 진입");
        Optional<Member> member = Optional.of(memberRepository.findByEmail(passwordResetRequestDTO.getEmail())
                .orElseThrow(()-> new MemberNotFoundException("회원정보가 일치하지 않습니다.")));

        member.get().setPassword(passwordEncoder.encode(passwordResetRequestDTO.getNewPassword()));

    }


}
