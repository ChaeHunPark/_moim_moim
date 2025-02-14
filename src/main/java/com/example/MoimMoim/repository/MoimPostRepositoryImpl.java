package com.example.MoimMoim.repository;

import com.example.MoimMoim.domain.MoimPost;
import com.example.MoimMoim.domain.QMoimPost;
import com.example.MoimMoim.domain.QMoimPostComment;
import com.example.MoimMoim.dto.moim.MoimPostSummaryResponseDTO;
import com.example.MoimMoim.enums.Category;
import com.example.MoimMoim.enums.MoimStatus;
import com.example.MoimMoim.service.utilService.DateTimeUtilService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class MoimPostRepositoryImpl implements MoimPostRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;
    private final DateTimeUtilService dateTimeUtilService;

    @Autowired
    public MoimPostRepositoryImpl(JPAQueryFactory jpaQueryFactory, DateTimeUtilService dateTimeUtilService) {
        this.jpaQueryFactory = jpaQueryFactory;
        this.dateTimeUtilService = dateTimeUtilService;
    }

    @Override
    public List<MoimPostSummaryResponseDTO> getPostList(
            String category, String sortBy, Pageable pageable, String keyword,
            String searchBy, String region, String moimStatus) {

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
                    postResponseDTO.setCreateAt(dateTimeUtilService.formatForClient(post.getCreatedAt())); // 날짜 포맷
                    postResponseDTO.setNickname(post.getMember().getNickname()); // 작성자 닉네임
                    postResponseDTO.setCommentCount(commentCount); // 댓글 수
                    postResponseDTO.setViewCount(post.getViewCount()); // 조회 수

                    postResponseDTO.setCurrentParticipants(post.getCurrentParticipants());
                    postResponseDTO.setMaxParticipants(post.getMaxParticipants());
                    postResponseDTO.setMoimStatus(post.getMoimStatus());
                    postResponseDTO.setMoimDate(dateTimeUtilService.formatForClient(post.getMoimDate()));
                    postResponseDTO.setRegion(post.getRegion());

                    return postResponseDTO;
                })
                .collect(Collectors.toList());
    }
}
