package com.example.MoimMoim.repository;

import com.example.MoimMoim.domain.MoimPost;
import com.example.MoimMoim.domain.QMoimPost;
import com.example.MoimMoim.domain.QMoimPostComment;
import com.example.MoimMoim.dto.moimPost.MoimPostPageResponseDTO;
import com.example.MoimMoim.dto.moimPost.MoimPostSummaryResponseDTO;
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
    public MoimPostPageResponseDTO<MoimPostSummaryResponseDTO> getPostList(
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

        // 4. 검색 조건, keyword가 존재해야 실행
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

        long totalElements = query.fetch().size(); // 필터링된 전체 게시글의 개수 계산

        // 5. 정렬 기준 (옵션),(댓글순, 조회수순)
        if ("date-asc".equalsIgnoreCase(sortBy)){
            query.orderBy(moimPost.createdAt.asc());
        } else if("date-desc".equalsIgnoreCase(sortBy)){
            query.orderBy(moimPost.createdAt.desc());
        } else if ("views".equalsIgnoreCase(sortBy)){
            query.orderBy(moimPost.viewCount.desc());
        } else if ("comment".equalsIgnoreCase(sortBy)){
            query.orderBy(moimPostComment.count().desc());
        }

        // 6. 페이지 번호 유효성 검사

        // 전체 엘리먼트 / 30개씩, 82 / 30 = 3페이지, 82개의 게시물이 있으면 3페이지.
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize()); // 총 페이지 수 계산
        int currentPage = pageable.getPageNumber(); // 0-based index
        if (currentPage >= totalPages) {
            currentPage = 0; // 페이지 넘버가 총 페이지 수보다 크다면 0페이지를 반환
        }

        // 페이지 번호로 결과 반환

        /*
        * 1페이지면 0 * 30 = 0 인덱스 부터 30개 씩
        * 2페이지면 1 * 30 = 30 인덱스 부터 30개 씩
        * 3페이지면 60 인덱스 부터 30개 씩
        *
        * */

        List<Tuple> results = query
                .offset((long) currentPage * pageable.getPageSize())
                .limit(pageable.getPageSize()) // 30개씩
                .fetch();

        List<MoimPostSummaryResponseDTO> moimPosts = results.stream()
                .map(tuple -> {
                    MoimPost post = tuple.get(moimPost);          // 게시글 엔티티
                    long commentCount = Optional.ofNullable(tuple.get(moimPostComment.count())).orElse(0L);
                    ;// 댓글 수 0일 경우 0을 반환

                    // DTO 변환
                    MoimPostSummaryResponseDTO postResponseDTO = new MoimPostSummaryResponseDTO();
                    postResponseDTO.setPostId(post.getMoimPostId());  // 게시글 ID
                    postResponseDTO.setTitle(post.getTitle()); // 게시글 제목
                    postResponseDTO.setCategory(post.getCategory().getLabel()); // 카테고리
                    postResponseDTO.setCreateAt(dateTimeUtilService.formatForClient(post.getCreatedAt())); // 날짜 포맷
                    postResponseDTO.setNickname(post.getMember().getNickname()); // 작성자 닉네임
                    postResponseDTO.setCommentCount(commentCount); // 댓글 수
                    postResponseDTO.setViewCount(post.getViewCount()); // 조회 수

                    postResponseDTO.setCurrentParticipants(post.getCurrentParticipants());
                    postResponseDTO.setMaxParticipants(post.getMaxParticipants());
                    postResponseDTO.setMoimStatus(post.getMoimStatus().getDisplayName());
                    postResponseDTO.setMoimDate(dateTimeUtilService.formatForClient(post.getMoimDate()));
                    postResponseDTO.setRegion(post.getRegion());

                    return postResponseDTO;
                })
                .collect(Collectors.toList());


        // DB OFFSET은 -1된 값을 원하기 때문에 -1값으로 들어오며, 돌려줘야 하는 값 페이지는 다시 +1 해준다.
        return new MoimPostPageResponseDTO<>(
                moimPosts,
                totalPages,
                totalElements,
                currentPage + 1,
                pageable.getPageSize()
        );
    }
}
