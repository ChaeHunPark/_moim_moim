package com.example.MoimMoim.repository;

import com.example.MoimMoim.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
