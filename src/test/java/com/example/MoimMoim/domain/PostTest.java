package com.example.MoimMoim.domain;

import com.example.MoimMoim.enums.Category;
import com.example.MoimMoim.enums.Gender;
import com.example.MoimMoim.enums.RoleName;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PostTest {

    private Member member;
    private Comment comment;
    private Role role;

    @BeforeEach
    void setup() {

        role = new Role(1L, RoleName.ROLE_USER);

        member = Member.builder()
                .email("test@example.com")
                .password("securePassword123")
                .phone("010-1234-5678")
                .name("홍길동")
                .gender(Gender.MALE)
                .nickname("길동이")
                .birthday(LocalDate.of(1990,1,1))
                .signupDate(LocalDateTime.now())
                .role(role)
                .build();
    }

    @Test
    @DisplayName("게시글 엔티티 생성 테스트")
    void createPost() {

        Post post = Post.builder()
                .title("제목")
                .category(Category.ART)
                .content("내용")
                .createAt(LocalDateTime.now())
                .updateAt(null)
                .viewCount(0L)
                .member(member)
                .comments(new ArrayList<>())
                .build();

        assertThat(post.getTitle()).isEqualTo("제목");
        assertThat(post.getContent()).isEqualTo("내용");
        assertThat(post.getComments()).isEmpty();
        assertThat(post.getViewCount()).isEqualTo(0L);
        assertThat(post.getCategory()).isEqualTo(Category.ART);
        assertThat(post.getCreateAt()).isNotNull();
        assertThat(post.getUpdateAt()).isNull();
        assertThat(post.getMember()).isEqualTo(member);

    }

    @Test
    @DisplayName("게시글 수정 테스트")
    void updatePost() {
        // 기존 게시글 생성
        Post post = Post.builder()
                .title("기존 제목")
                .category(Category.ART)
                .content("기존 내용")
                .createAt(LocalDateTime.now())
                .updateAt(null)
                .viewCount(0L)
                .member(member)
                .comments(new ArrayList<>())
                .build();

        LocalDateTime beforeUpdate = post.getUpdateAt();

        // 게시글 수정
        post.setTitle("새로운 제목");
        post.setContent("새로운 내용");
        post.setCategory(Category.STUDY);
        post.setUpdateAt(LocalDateTime.now());


        // 검증
        assertThat(post.getTitle()).isEqualTo("새로운 제목");
        assertThat(post.getContent()).isEqualTo("새로운 내용");
        assertThat(post.getCategory()).isEqualTo(Category.STUDY);
        assertThat(post.getUpdateAt()).isNotEqualTo(beforeUpdate);
    }

    @Test
    @DisplayName("게시글 조회수 증가 테스트")
    void increaseViewCount() {
        // Given
        Post post = Post.builder()
                .title("테스트 제목")
                .category(Category.ART)
                .content("테스트 내용")
                .createAt(LocalDateTime.now())
                .updateAt(null)
                .viewCount(0L)
                .member(member)
                .comments(new ArrayList<>())
                .build();

        // When
        post.incrementViewCount();

        // Then
        assertThat(post.getViewCount()).isEqualTo(1L);
    }

    @Test
    @DisplayName("게시글에 댓글 추가 테스트")
    void addComment() {
        // Given
        List<Comment> comments = new ArrayList<>();

        Post post = Post.builder()
                .title("테스트 제목")
                .category(Category.ART)
                .content("테스트 내용")
                .createAt(LocalDateTime.now())
                .viewCount(0L)
                .member(member)
                .comments(comments)
                .build();

        Comment comment = Comment.builder()
                .content("댓글 내용")
                .member(member)
                .post(post)
                .createAt(LocalDateTime.now())
                .build();

        // When
        comments.add(comment);

        // Then
        assertThat(post.getComments()).hasSize(1);
        assertThat(post.getComments().get(0).getContent()).isEqualTo("댓글 내용");

    }

    @Test
    @DisplayName("게시글 댓글 삭제 테스트")
    void deletePostWithComments() {
        // Given
        List<Comment> comments = new ArrayList<>();

        Post post = Post.builder()
                .title("테스트 제목")
                .category(Category.ART)
                .content("테스트 내용")
                .createAt(LocalDateTime.now())
                .viewCount(0L)
                .member(member)
                .comments(comments)
                .build();

        Comment comment = Comment.builder()
                .content("댓글 내용")
                .member(member)
                .post(post)
                .createAt(LocalDateTime.now())
                .build();

        // When
        comments.add(comment);
        comments.clear();

        // Then
        assertThat(post.getComments()).isEmpty();
    }



}