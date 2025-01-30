package com.example.MoimMoim.domain;

import com.example.MoimMoim.enums.ParticipationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class MoimParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long moimParticipationRequestId;

    private String intro;

    private String reasonParticipation;

    private String rejection_reason;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING) // Enum을 문자열로 저장
    private ParticipationStatus participationStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", unique = true) // 특정 게시글에 대해 한 번만 신청 할수 있다.
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moim_post_id")
    private MoimPost moimPost;

    @OneToMany(mappedBy = "moimParticipation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MoimAccptedMember> moimAccptedMembers;

}
