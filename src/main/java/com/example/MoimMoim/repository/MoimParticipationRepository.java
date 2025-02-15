package com.example.MoimMoim.repository;

import com.example.MoimMoim.domain.Member;
import com.example.MoimMoim.domain.MoimParticipation;
import com.example.MoimMoim.domain.MoimPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MoimParticipationRepository extends JpaRepository<MoimParticipation, Long> {
    // 특정 모임 게시글과 회원 ID로 중복 신청 여부를 확인
    boolean existsByMoimPostAndMember(MoimPost moimPost, Member member);

    // Member 기준으로 찾는다.
    List<MoimParticipation> findByMember(Member member);

    // MoimPost 기준으로 찾는다.
    List<MoimParticipation> findByMoimPost(MoimPost moimPost);

}
