package com.example.MoimMoim.domain;


import com.example.MoimMoim.enums.Gender;
import com.example.MoimMoim.enums.RoleName;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentTest {

    private final Member member = new Member();
    private final Post post = new Post();

    @BeforeEach
    void setup() {
    }

    @Test
    @DisplayName("댓글 엔티티 생성 테스트")
    void createComment() {
        Comment comment = Comment.builder()
                .content("댓글 내용")
                .member(member)
                .post(post)
                .createAt(LocalDateTime.now())
                .build();

        assertThat(comment.getContent()).isEqualTo("댓글 내용");
    }

    @Test
    @DisplayName("댓글 엔티티 수정 테스트")
    void updateComment() {
        // given
        Comment comment = Comment.builder()
                .content("댓글 내용")
                .member(member)
                .post(post)
                .createAt(LocalDateTime.now())
                .build();
        String beforeUpdate = comment.getContent();

        //when
        comment.setContent("새로운 댓글 내용");

        //then
        assertThat(comment.getContent()).isNotEqualTo(beforeUpdate);
        assertThat(comment.getContent()).isEqualTo("새로운 댓글 내용");
    }



}
