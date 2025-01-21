package com.example.MoimMoim.repository;

import com.example.MoimMoim.domain.MoimPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoimPostRepository extends JpaRepository<MoimPost, Long> {
}
