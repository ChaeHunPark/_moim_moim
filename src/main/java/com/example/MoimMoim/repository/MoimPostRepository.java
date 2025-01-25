package com.example.MoimMoim.repository;

import com.example.MoimMoim.domain.Member;
import com.example.MoimMoim.domain.MoimPost;
import com.example.MoimMoim.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MoimPostRepository extends JpaRepository<MoimPost, Long> {
    // moimPostId와 member로 게시글 조회
    Optional<MoimPost> findByMoimPostIdAndMember(Long moimPostId, Member member);
}
