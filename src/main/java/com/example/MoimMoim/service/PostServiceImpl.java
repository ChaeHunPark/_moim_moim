package com.example.MoimMoim.service;

import com.example.MoimMoim.domain.Member;
import com.example.MoimMoim.domain.Post;
import com.example.MoimMoim.domain.QComment;
import com.example.MoimMoim.domain.QPost;
import com.example.MoimMoim.dto.comment.CommentResponseDTO;
import com.example.MoimMoim.dto.post.PostResponseDTO;
import com.example.MoimMoim.dto.post.PostRequestDTO;
import com.example.MoimMoim.dto.post.PostSummaryResponseDTO;
import com.example.MoimMoim.enums.Category;
import com.example.MoimMoim.exception.member.MemberNotFoundException;
import com.example.MoimMoim.exception.post.PostNotFoundException;
import com.example.MoimMoim.repository.MemberRepository;
import com.example.MoimMoim.repository.PostRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PostServiceImpl implements PostService{

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, MemberRepository memberRepository, JPAQueryFactory jpaQueryFactory) {
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
        this.jpaQueryFactory = jpaQueryFactory;
    }


    public Member findMember(Long id){
        return memberRepository.findById(id).
                orElseThrow(() -> new MemberNotFoundException("회원이 존재하지 않습니다."));
    }



    public Post convertPost(PostRequestDTO postRequestDTO){
        return Post.builder()
                    .title(postRequestDTO.getTitle())
                    .category(postRequestDTO.getCategory())
                    .content(postRequestDTO.getContent())
                    .createAt(LocalDateTime.now())
                    .viewCount(0L)
                    .member(findMember(postRequestDTO.getMemberId()))
                    .build();
    }


    //시간 포맷팅
    private String formatDate(LocalDateTime createAt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd. HH:mm");
        return createAt.format(formatter);
    }


    @Transactional
    @Override
    public void createPost(PostRequestDTO postRequestDTO) {
        Post post = convertPost(postRequestDTO);
        postRepository.save(post);
    }

    // 게시글 단건 조회
    @Override
    public PostResponseDTO viewPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글이 존재하지 않습니다."));

        // 게시글 조회 후 카운트 증가 및 데이터베이스에 저장
        post.incrementViewCount();
        postRepository.save(post);

        // 댓글리스트 조회
        List<CommentResponseDTO> comments = post.getComments().stream()
                .map(comment -> new CommentResponseDTO(comment.getContent(), comment.getMember().getNickname(), formatDate(comment.getCreateAt())))
                .collect(Collectors.toList());



        return PostResponseDTO.builder()
                .title(post.getTitle())
                .category(post.getCategory())
                .content(post.getContent())
                .nickname(post.getMember().getNickname())
                .createAt(formatDate(post.getCreateAt()))
                .commentList(comments)
                .viewCount(post.getViewCount())
                .build();
    }

    // 전체 게시글 조회
    @Override
    public List<PostSummaryResponseDTO> getPostList(String category,
                                                    String sortBy,
                                                    Pageable pageable,
                                                    String keyword,
                                                    String searchBy) {

        QPost post = QPost.post;
        QComment comment = QComment.comment;

        JPAQuery<Tuple> query = jpaQueryFactory.select(
                        post,                            // 게시글 정보
                        comment.count()                  // 댓글 개수
                        )
                .from(post)
                .leftJoin(post.comments, comment)   // 게시글과 댓글 조인
                .groupBy(post.postId);                   // 게시글 ID 기준으로 그룹화


        BooleanBuilder whereClause = new BooleanBuilder();


        // 1. 카테고리가 있으면 카테고리를 필터링한다.
        if (category != null && !category.isEmpty()){
            whereClause.and(post.category.eq(Category.valueOf(category)));
        }

        // 2. 검색 조건, keyword가 존재해야 실행
        if(keyword != null && !keyword.isBlank()){
            switch (searchBy) {
            case "title":
                whereClause.and(post.title.containsIgnoreCase(keyword));
                break;
            case "content":
                whereClause.and(post.content.containsIgnoreCase(keyword));
                break;
            case "title+content": // 제목과 내용 모두에서 검색
                whereClause.and(
                        post.title.containsIgnoreCase(keyword)
                                .or(post.content.containsIgnoreCase(keyword))
                );
                break;
            default:
                throw new IllegalArgumentException("Invalid searchBy parameter: " + searchBy);
                }
        }

        query.where(whereClause);


        // 2.정렬 기준 (옵션),(댓글순, 조회수순)
        if ("date-asc".equalsIgnoreCase(sortBy)){
            query.orderBy(post.createAt.asc());
        } else if("date-desc".equalsIgnoreCase(sortBy)){
            query.orderBy(post.createAt.desc());
        } else if ("views".equalsIgnoreCase(sortBy)){
            query.orderBy(post.viewCount.desc());
        } else if ("comment".equalsIgnoreCase(sortBy)){
            query.orderBy(comment.count().desc());
        }

        //반환사이즈 조정
        List<Tuple> results = query
                .offset(pageable.getOffset())// pageable에 page값이 1이 들어가면 (1 - 1) * size로 계산 = 0부터
                .limit(pageable.getPageSize()) // 40개씩
                .fetch();

        return results.stream()
                .map(tuple -> {
                    Post postEntity = tuple.get(post);          // 게시글 엔티티
                    long commentCount = tuple.get(comment.count()); // 댓글 수

                    // DTO 변환
                    PostSummaryResponseDTO postResponseDTO = new PostSummaryResponseDTO();
                    postResponseDTO.setPostId(postEntity.getPostId());  // 게시글 ID
                    postResponseDTO.setTitle(postEntity.getTitle()); // 게시글 제목
                    postResponseDTO.setCategory(postEntity.getCategory()); // 카테고리
                    postResponseDTO.setCreateAt(formatDate(postEntity.getCreateAt())); // 날짜 포맷
                    postResponseDTO.setNickname(postEntity.getMember().getNickname()); // 작성자 닉네임
                    postResponseDTO.setCommentCount(commentCount); // 댓글 수
                    postResponseDTO.setViewCount(postEntity.getViewCount()); // 조회 수

                    return postResponseDTO;
                })
                .collect(Collectors.toList());


    }


//        List<Post> posts = postRepository.findAll();
//
//        return posts.stream()
//                .map(post -> {
//                    // Post 객체를 PostResponseDTO로 변환
//                    PostSummaryResponseDTO postResponseDTO = new PostSummaryResponseDTO();
//                    postResponseDTO.setPostId(post.getPostId());
//                    postResponseDTO.setTitle(post.getTitle());
//                    postResponseDTO.setCategory(post.getCategory()); // Enum 타입 그대로 사용
//                    postResponseDTO.setCreateAt(formatDate(post.getCreateAt())); // formatDate 메서드를 사용해 날짜 포맷팅
//                    postResponseDTO.setNickname(post.getMember().getNickname()); // Member 객체에서 nickname 가져오기
//                    postResponseDTO.setCommentCount((long) post.getComments().size());
//                    postResponseDTO.setViewCount(post.getViewCount());
//                    return postResponseDTO;
//                })
//                .collect(Collectors.toList()); // 리스트 반환
//    }

    @Override
    public void updatePost(Long postId, PostRequestDTO postRequestDTO) {
        // 1. 포스트 아이디로 게시글 찾기
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다."));


        // 2. 게시글 수정
        post.setTitle(postRequestDTO.getTitle());
        post.setCategory(postRequestDTO.getCategory());
        post.setContent(post.getContent());

        postRepository.save(post);
    }

    @Override
    public void deletePost(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
        postRepository.delete(post);
    }

}
