package com.example.MoimMoim.dto.moimParticipation;


import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class MoimParticipationRequestDTO {

    @Size(max = 500, message = "소개는 최대 500자까지 가능합니다.")
    private String intro;

    @Size(max = 1000, message = "참여 이유는 최대 1000자까지 가능합니다.")
    private String reasonParticipation;

}
