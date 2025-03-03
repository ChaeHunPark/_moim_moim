package com.example.MoimMoim.dto.moimPost;

import com.example.MoimMoim.enums.Category;
import com.example.MoimMoim.enums.MoimStatus;
import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MoimPostResponseDTO {
    private Long memberId; // 작성자 ID
    private Long moimPostId;
    private String title; // 제목
    private String content; // 내용
    private String location; // 장소
    private String address; // 주소
    private String roadAddress; // 도로명 주소
    private String region;
    private Double mapx; // 경도
    private Double mapy; // 위도
    private int currentParticipants; // 현재 참여 인원
    private int maxParticipants; // 최대 참여 인원
    private String category; // 카테고리
    private String moimStatus; // 모임 상태
    private Long viewCount; // 조회수
    private String moimDate; // 모임 날짜
    private String createdAt; // 생성 날짜
    private String updateAt;
    private List<MoimCommentResponseDTO> moimCommentList;

    private String cancellationReason; // 취소 이유
}
