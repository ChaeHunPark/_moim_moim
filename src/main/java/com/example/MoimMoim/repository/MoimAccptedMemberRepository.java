package com.example.MoimMoim.repository;

import com.example.MoimMoim.domain.MoimAccptedMember;
import com.example.MoimMoim.domain.MoimParticipation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MoimAccptedMemberRepository extends JpaRepository<MoimAccptedMember, Long> {
    List<MoimAccptedMember> findByMoimParticipationIn(List<MoimParticipation> moimParticipation);
}
