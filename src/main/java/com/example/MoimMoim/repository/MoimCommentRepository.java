package com.example.MoimMoim.repository;

import com.example.MoimMoim.domain.Member;
import com.example.MoimMoim.domain.MoimPostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MoimCommentRepository extends JpaRepository<MoimPostComment, Long> {
    Optional<MoimPostComment> findByMoimCommentIdAndMember(Long moimCommentId, Member member);
}
