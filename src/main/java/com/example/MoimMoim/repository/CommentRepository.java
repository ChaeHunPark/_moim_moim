package com.example.MoimMoim.repository;

import com.example.MoimMoim.domain.Comment;
import com.example.MoimMoim.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByCommentIdAndMember(Long commentId, Member member);
}
