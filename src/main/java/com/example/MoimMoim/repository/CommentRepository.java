package com.example.MoimMoim.repository;

import com.example.MoimMoim.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
