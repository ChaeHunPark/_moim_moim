package com.example.MoimMoim.service.authService;

import com.example.MoimMoim.exception.auth.VerificationCodeExpiredException;
import com.example.MoimMoim.exception.auth.VerificationCodeMismatchException;
import com.example.MoimMoim.exception.auth.VerificationCodeNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VerificationCodeManagerImpl implements VerificationCodeManager {



    private static final long VERIFICATION_CODE_EXPIRATION_TIME = 5 * 60 * 1000; // 인증번호 유효시간 5분
    private static final long VERIFICATION_CODE_RETRY_TIME = 1 * 60 * 1000; // 인증번호 재요청 시간 1분


    /*
    *
    * Spring에서 @Service 또는 @Component로 등록된 빈(Bean)은 기본적으로 싱글톤으로 관리된다.
    * 이렇게 되면 VerificationCodeManager 인스턴스는 애플리케이션 전체에서 단 하나만 존재하며, 모든 요청에서 공유된다.
    *
    * 한 유저의 요청의 독립성을 유지하기 위한 HashMap
    */

    // 멀티스레드 안전성을 고려해 HashMap 대신 ConcurrentHashMap을 사용하는 것이 권장.
    private final Map<String, VerificationData> verificationCodeMap = new ConcurrentHashMap<>();

    // 인증번호를 메모리에 저장
    @Override
    public void saveVerificationCode(String email, String code) {
        VerificationData data = new VerificationData(code, System.currentTimeMillis());
        verificationCodeMap.put(email, data);
    }

    // 인증번호 유효성 검사
    @Override
    public void validateVerificationCode(String email, String userInputCode) {
        // 해쉬맵에서 이메일 정보를 가져온다.
        VerificationData data = verificationCodeMap.get(email);


        // 인증 정보가 존재하지 않음
        if (data == null) {
            throw new VerificationCodeNotFoundException("인증정보가 존재하지 않습니다.");
        }

        long currentTime = System.currentTimeMillis(); // 현재 시스템 시각을 가져옴

        // 인증번호 유효기간 체크
        // 현재시간 - 유저 타임스탬프 = 남은 유효시간 > 설정 유효시간
        if (currentTime - data.getTimestamp() > VERIFICATION_CODE_EXPIRATION_TIME) {
            verificationCodeMap.remove(email); // 만료된 인증정보 삭제
            throw new VerificationCodeExpiredException("인증정보가 만료되었습니다.");
        }

        // 인증번호 비교
        if (!data.getCode().equals(userInputCode)) {
            throw new VerificationCodeMismatchException("인증번호가 일치하지 않습니다.");
        }

        verificationCodeMap.remove(email); // 인증 성공 시 삭제
    }

    // 인증번호 재요청 가능 여부 체크 keep
    @Override
    public boolean canRequestNewCode(String email) {


        VerificationData data = verificationCodeMap.get(email);

        // 이메일에 해당하는 인증번호가 없으면 새로 요청할 수 있음
        if (data == null) {
            return true;
        }

        long currentTime = System.currentTimeMillis();
        long timeDifference = currentTime - data.getTimestamp(); // 시간 차이 계산

        // 시간 차이가 1분 이상이면 재요청 가능, 기존 인증번호 삭제
        if (timeDifference >= VERIFICATION_CODE_RETRY_TIME) {
            verificationCodeMap.remove(email); // 인증번호 삭제
            return true;
        }

        // 1분 이내에는 재요청 불가
        return false;
    }

    // 인증번호 삭제 (인증 성공 후)
    @Override
    public void removeVerificationCode(String email) {
        verificationCodeMap.remove(email);
    }


    // 내부 클래스: 인증번호와 생성 시간 관리
    private static class VerificationData {
        private final String code;
        private final long timestamp;

        public VerificationData(String code, long timestamp) {
            this.code = code;
            this.timestamp = timestamp;
        }

        public String getCode() {
            return code;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}