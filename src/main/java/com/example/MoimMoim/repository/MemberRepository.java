package com.example.MoimMoim.repository;

import com.example.MoimMoim.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    // 이메일이 존재하는가?
    boolean existsByEmail(String email);

    //이메일, 전화번호 기준으로 리턴, 객체를 반환하지 않을 시에는 boolean이 적합하다.
    boolean existsByNameAndEmail(String name, String email);

    //이메일 기준으로 찾기
    Optional<Member> findByEmail(String email);

}
