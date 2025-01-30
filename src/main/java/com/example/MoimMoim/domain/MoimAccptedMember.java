package com.example.MoimMoim.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class MoimAccptedMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long moimAcceptedMemberId;

    private LocalDateTime acceptedAt;

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moim_participation_request_id")
    private MoimParticipation moimParticipation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

}
