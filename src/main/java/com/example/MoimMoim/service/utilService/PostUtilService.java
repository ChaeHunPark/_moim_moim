package com.example.MoimMoim.service.utilService;

import com.example.MoimMoim.domain.Member;
import com.example.MoimMoim.exception.member.MemberNotFoundException;
import com.example.MoimMoim.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PostUtilService {

    // 클라이언트에 반환할 시간 포맷팅
    public String formatForClient(LocalDateTime createAt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd. HH:mm");
        return createAt.format(formatter);
    }

    // 문자열을 LocalDateTime으로 변환 (DB에 저장 시 사용)
    public LocalDateTime parseToLocalDateTime(String formattedDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd. HH:mm");
        return LocalDateTime.parse(formattedDate, formatter);
    }


    public Member findMember(Long id, MemberRepository repository){
        return repository.findById(id).
                orElseThrow(() -> new MemberNotFoundException("회원이 존재하지 않습니다."));
    }
}
