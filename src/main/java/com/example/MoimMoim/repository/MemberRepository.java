package com.example.MoimMoim.repository;

import com.example.MoimMoim.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    // 이메일이 존재하는가?
    boolean existsByEmail(String email);
}
