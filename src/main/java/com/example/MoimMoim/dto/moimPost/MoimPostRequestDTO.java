package com.example.MoimMoim.dto.moimPost;

import com.example.MoimMoim.dto.post.PostRequestDTO;
import com.example.MoimMoim.enums.Category;
import com.example.MoimMoim.enums.MoimStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MoimPostRequestDTO {

    @NotNull(message = "작성자의 ID는 필수 입력 항목입니다.")
    private Long memberId;

    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    private String title;

    @NotNull(message = "카테고리는 필수 입력 항목입니다.")
    private Category category;

    @NotBlank(message = "내용은 필수 입력 항목입니다.")
    private String content;

    @NotBlank(message = "장소 제목은 필수 입력 값입니다.")
    private String location;

    @NotBlank(message = "주소는 필수 입력 값입니다.")
    private String address;

    @NotBlank(message = "도로명 주소는 필수 입력 값입니다.")
    private String roadAddress;

    @NotNull(message = "지도 정보가 정확하지 않습니다.")
    private Double mapx; // 경도

    @NotNull(message = "지도 정보가 정확하지 않습니다.")
    private Double mapy; // 위도

    @NotNull(message = "인원은 필수입니다.")
    private int maxParticipants; //최대 참여 인원
    //    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // 요청 파라미터 (RequestParam 등)에서 사용
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss") // JSON 요청에서 사용 , "2025-01-13T14:30:00"
    @NotNull(message = "모임 날짜는 필수입니다.")
    private LocalDateTime moimDate; // 모임 날짜




}
