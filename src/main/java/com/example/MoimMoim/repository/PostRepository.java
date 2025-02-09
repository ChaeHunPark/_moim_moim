package com.example.MoimMoim.repository;

import com.example.MoimMoim.domain.Member;
import com.example.MoimMoim.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
    Optional<Post> findByPostIdAndMember(Long postId, Member member);
}
