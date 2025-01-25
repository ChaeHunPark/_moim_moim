package com.example.MoimMoim.service.moimPostService;

import com.example.MoimMoim.domain.*;
import com.example.MoimMoim.dto.moimPost.MoimCommentResponseDTO;
import com.example.MoimMoim.dto.moimPost.MoimPostRequestDTO;
import com.example.MoimMoim.dto.moimPost.MoimPostResponseDTO;
import com.example.MoimMoim.dto.moimPost.MoimPostSummaryResponseDTO;
import com.example.MoimMoim.enums.Category;
import com.example.MoimMoim.enums.MoimStatus;
import com.example.MoimMoim.exception.member.MemberNotFoundException;
import com.example.MoimMoim.exception.post.PostNotFoundException;
import com.example.MoimMoim.repository.MemberRepository;
import com.example.MoimMoim.repository.MoimPostRepository;
import com.example.MoimMoim.service.utilService.PostUtilService;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MoimPostServiceImpl implements MoimPostService{

    private final MemberRepository memberRepository;
    private final PostUtilService postUtilService;
    private final MoimPostRepository moimPostRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Autowired
    public MoimPostServiceImpl(MemberRepository memberRepository, PostUtilService postUtilService, MoimPostRepository moimPostRepository, JPAQueryFactory jpaQueryFactory) {
        this.memberRepository = memberRepository;
        this.postUtilService = postUtilService;
        this.moimPostRepository = moimPostRepository;
        this.jpaQueryFactory = jpaQueryFactory;
    }

    private String extractRegionFromData(String address){
        return address.split(" ")[0];
    }

    private MoimPost convertMoimPost(MoimPostRequestDTO moimPostRequestDTO){
        return MoimPost.builder()
                .member(postUtilService.findMember(moimPostRequestDTO.getMemberId(), memberRepository))
                .title(moimPostRequestDTO.getTitle())
                .category(moimPostRequestDTO.getCategory())
                .content(moimPostRequestDTO.getContent())
                .location(moimPostRequestDTO.getLocation())
                .address(moimPostRequestDTO.getAddress())
                .region(extractRegionFromData(moimPostRequestDTO.getAddress()))
                .roadAddress(moimPostRequestDTO.getRoadAddress())
                .mapx(moimPostRequestDTO.getMapx())
                .mapy(moimPostRequestDTO.getMapy())
                .maxParticipants(moimPostRequestDTO.getMaxParticipants())
                .currentParticipants(1)
                .moimDate(moimPostRequestDTO.getMoimDate())
                .moimStatus(MoimStatus.모집중)
                .createdAt(LocalDateTime.now())
                .updateAt(null)
                .viewCount(0L)
                .build();
    }


    @Transactional
    @Override
    public void createMoimPost(MoimPostRequestDTO moimPostRequestDTO) {
        MoimPost moimPost = convertMoimPost(moimPostRequestDTO);
        moimPostRepository.save(moimPost);
    }

    @Override
    public MoimPostResponseDTO viewMoimPost(Long postId) {
        MoimPost moimPost = moimPostRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글이 존재하지 않습니다."));

        moimPost.incrementViewCount();
        moimPostRepository.save(moimPost);


        List<MoimCommentResponseDTO> comments = moimPost.getMoimComments().stream()
                .map(comment -> new MoimCommentResponseDTO(
                        comment.getContent(),
                        comment.getMember().getNickname(),
                        postUtilService.formatForClient(comment.getCreateAt())
                )).collect(Collectors.toList());



        MoimPostResponseDTO moimPostResponseDTO = MoimPostResponseDTO.builder()
                .memberId(moimPost.getMember().getMemberId())
                .title(moimPost.getTitle())
                .content(moimPost.getContent())
                .location(moimPost.getLocation())
                .address(moimPost.getAddress())
                .roadAddress(moimPost.getRoadAddress())
                .region(moimPost.getRegion())
                .mapx(moimPost.getMapx())
                .mapy(moimPost.getMapy())
                .currentParticipants(moimPost.getCurrentParticipants())
                .maxParticipants(moimPost.getMaxParticipants())
                .category(moimPost.getCategory())
                .moimStatus(moimPost.getMoimStatus())
                .viewCount(moimPost.getViewCount())
                .moimDate(postUtilService.formatForClient(moimPost.getMoimDate()))
                .createdAt(postUtilService.formatForClient(moimPost.getCreatedAt()))
                .moimCommentList(comments)
                .build();

        // 업데이트가 null 일수도 있다.
        if(moimPostResponseDTO.getUpdateAt() != null){
            moimPostResponseDTO.setUpdateAt(postUtilService.formatForClient(moimPost.getUpdateAt()));
        }



        return moimPostResponseDTO;


    }

    @Override
    public List<MoimPostSummaryResponseDTO> getPostList(String category,
                                                        String sortBy,
                                                        Pageable pageable,
                                                        String keyword,
                                                        String searchBy,
                                                        String region,
                                                        String moimStatus) {
        QMoimPost moimPost = QMoimPost.moimPost;
        QMoimPostComment moimPostComment = QMoimPostComment.moimPostComment;

        JPAQuery<Tuple> query = jpaQueryFactory.select(
                        moimPost,                            // 게시글 정보
                        moimPostComment.count()                  // 댓글 개수
                )
                .from(moimPost)
                .leftJoin(moimPost.moimComments, moimPostComment)   // 게시글과 댓글 조인
                .groupBy(moimPost.moimPostId);                   // 게시글 ID 기준으로 그룹화


        BooleanBuilder whereClause = new BooleanBuilder();


        // 1. 카테고리가 있으면 카테고리를 필터링한다.
        if (category != null && !category.isEmpty()){
            whereClause.and(moimPost.category.eq(Category.valueOf(category)));
        }
        // 2. 행정구역 있으면 필터링 한다.
        if (region != null && !region.isEmpty()) {
            whereClause.and(moimPost.region.eq(region));
        }

        // 3. 모임 상태 별 필터링
        if(moimStatus != null && !moimStatus.isEmpty()){
            whereClause.and(moimPost.moimStatus.eq(MoimStatus.valueOf(moimStatus)));
        }

        // 2. 검색 조건, keyword가 존재해야 실행
        if(keyword != null && !keyword.isBlank()){
            switch (searchBy) {
                case "title":
                    whereClause.and(moimPost.title.containsIgnoreCase(keyword)); // %title%
                    break;
                case "content":
                    whereClause.and(moimPost.content.containsIgnoreCase(keyword));
                    break;
                case "title+content": // 제목과 내용 모두에서 검색
                    whereClause.and(moimPost.title.containsIgnoreCase(keyword)
                                    .or(moimPost.content.containsIgnoreCase(keyword))
                    );
                    break;
                default:
                    throw new IllegalArgumentException("Invalid searchBy parameter: " + searchBy);
            }
        }

        query.where(whereClause);


        // 2.정렬 기준 (옵션),(댓글순, 조회수순)
        if ("date-asc".equalsIgnoreCase(sortBy)){
            query.orderBy(moimPost.createdAt.asc());
        } else if("date-desc".equalsIgnoreCase(sortBy)){
            query.orderBy(moimPost.createdAt.desc());
        } else if ("views".equalsIgnoreCase(sortBy)){
            query.orderBy(moimPost.viewCount.desc());
        } else if ("comment".equalsIgnoreCase(sortBy)){
            query.orderBy(moimPostComment.count().desc());
        }

        //반환사이즈 조정
        List<Tuple> results = query
                .offset(pageable.getOffset())// pageable에 page값이 1이 들어가면 (1 - 1) * size로 계산 = 0부터
                .limit(pageable.getPageSize()) // 40개씩
                .fetch();

        return results.stream()
                .map(tuple -> {
                    MoimPost post = tuple.get(moimPost);          // 게시글 엔티티
                    long commentCount = Optional.ofNullable(tuple.get(moimPostComment.count())).orElse(0L);;// 댓글 수 0일 경우 0을 반환

                    // DTO 변환
                    MoimPostSummaryResponseDTO postResponseDTO = new MoimPostSummaryResponseDTO();
                    postResponseDTO.setPostId(post.getMoimPostId());  // 게시글 ID
                    postResponseDTO.setTitle(post.getTitle()); // 게시글 제목
                    postResponseDTO.setCategory(post.getCategory()); // 카테고리
                    postResponseDTO.setCreateAt(postUtilService.formatForClient(post.getCreatedAt())); // 날짜 포맷
                    postResponseDTO.setNickname(post.getMember().getNickname()); // 작성자 닉네임
                    postResponseDTO.setCommentCount(commentCount); // 댓글 수
                    postResponseDTO.setViewCount(post.getViewCount()); // 조회 수

                    postResponseDTO.setCurrentParticipants(post.getCurrentParticipants());
                    postResponseDTO.setMaxParticipants(post.getMaxParticipants());
                    postResponseDTO.setMoimStatus(post.getMoimStatus());
                    postResponseDTO.setMoimDate(postUtilService.formatForClient(post.getMoimDate()));
                    postResponseDTO.setRegion(post.getRegion());

                    return postResponseDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void updatePost(Long moimPostId, MoimPostRequestDTO requestDTO) {
        Member member = memberRepository.findById(requestDTO.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException("회원 정보를 찾을 수 없습니다."));

        MoimPost moimPost = moimPostRepository.findByMoimPostIdAndMember(moimPostId, member)
                .orElseThrow(() -> new PostNotFoundException("게시글 정보를 찾을 수 없습니다."));

        moimPost.setTitle(moimPost.getTitle());
        moimPost.setContent(moimPost.getContent());
        moimPost.setLocation(requestDTO.getLocation());
        moimPost.setAddress(requestDTO.getAddress());
        moimPost.setRoadAddress(requestDTO.getRoadAddress());
        moimPost.setRegion(extractRegionFromData(requestDTO.getRoadAddress()));
        moimPost.setMapx(requestDTO.getMapx());
        moimPost.setMapy(requestDTO.getMapy());
        moimPost.setCategory(requestDTO.getCategory());
        moimPost.setMoimDate(requestDTO.getMoimDate());
        moimPost.setUpdateAt(LocalDateTime.now());
    }

    @Override
    public void deletePost(Long moimPostId, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("회원 정보를 찾을 수 없습니다."));

        MoimPost moimPost = moimPostRepository.findByMoimPostIdAndMember(moimPostId, member)
                .orElseThrow(() -> new PostNotFoundException("게시글 정보를 찾을 수 없습니다."));
        moimPostRepository.delete(moimPost);

    }


}
