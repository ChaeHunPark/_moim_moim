package com.example.MoimMoim.service.utilService;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DateTimeUtilService {

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

}
