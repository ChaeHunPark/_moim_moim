package com.example.MoimMoim.util;

import com.example.MoimMoim.domain.Member;
import com.example.MoimMoim.exception.member.MemberNotFoundException;
import com.example.MoimMoim.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PostUtilService {
    //시간 포맷팅
    public String formatDate(LocalDateTime createAt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd. HH:mm");
        return createAt.format(formatter);
    }

    public Member findMember(Long id, MemberRepository repository){
        return repository.findById(id).
                orElseThrow(() -> new MemberNotFoundException("회원이 존재하지 않습니다."));
    }

}
