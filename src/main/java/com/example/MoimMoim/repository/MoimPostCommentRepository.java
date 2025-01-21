package com.example.MoimMoim.repository;

import com.example.MoimMoim.domain.MoimPostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoimPostCommentRepository extends JpaRepository<MoimPostComment, Long> {
}
