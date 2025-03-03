package com.example.MoimMoim.repository;

import com.example.MoimMoim.domain.Post;
import com.example.MoimMoim.domain.QComment;
import com.example.MoimMoim.domain.QPost;
import com.example.MoimMoim.dto.post.PostPageResponseDTO;
import com.example.MoimMoim.dto.post.PostSummaryResponseDTO;
import com.example.MoimMoim.enums.Category;
import com.example.MoimMoim.exception.post.CategoryNotFoundException;
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
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Autowired
    public PostRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public PostPageResponseDTO<PostSummaryResponseDTO> findPostsByCategoryAndKeyword(String category, String keyword, String searchBy, String sortBy, Pageable pageable) {
        QPost post = QPost.post;
        QComment comment = QComment.comment;

        JPAQuery<Tuple> query = getQuery(post, comment);
        BooleanBuilder whereClause = new BooleanBuilder();

        if (category != null && !category.isEmpty()) {
            try {
                // 카테고리 값이 잘못되었을 경우 예외를 던짐
                Category categoryEnum = Category.valueOf(category);
                whereClause.and(post.category.eq(categoryEnum)); // 유효한 카테고리 값으로 필터링
            } catch (IllegalArgumentException e) {
                // 잘못된 카테고리 값에 대해서는 예외를 던짐
                throw new CategoryNotFoundException("카테고리 정보가 없습니다.");
            }
        }

        // 2. 검색 조건, keyword가 존재하면 실행
        if (keyword != null && !keyword.isBlank()) {
            switch (searchBy) {
                case "title":
                    whereClause.and(post.title.containsIgnoreCase(keyword)); // %title%
                    break;
                case "content":
                    whereClause.and(post.content.containsIgnoreCase(keyword));
                    break;
                case "title+content": // 제목과 내용 모두에서 검색
                    whereClause.and(post.title.containsIgnoreCase(keyword)
                            .or(post.content.containsIgnoreCase(keyword)));
                    break;
                default:
                    throw new IllegalArgumentException("Invalid searchBy parameter: " + searchBy);
            }
        }

        query.where(whereClause);

        long totalElements = query.fetch().size(); // 필터링된 전체 개수 계산

        // 3. 정렬 기준
        if ("date-asc".equalsIgnoreCase(sortBy)) {
            query.orderBy(post.createAt.asc());
        } else if ("date-desc".equalsIgnoreCase(sortBy)) {
            query.orderBy(post.createAt.desc());
        } else if ("views".equalsIgnoreCase(sortBy)) {
            query.orderBy(post.viewCount.desc());
        } else if ("comment".equalsIgnoreCase(sortBy)) {
            query.orderBy(comment.count().desc());
        }

        // 4. 페이지 번호 유효성 검사
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize()); // 총 페이지 수 계산
        int currentPage = pageable.getPageNumber(); // 0-based index
        if (currentPage >= totalPages) {
            currentPage = 0; // 페이지 넘버가 총 페이지 수보다 크면 첫 페이지로 리다이렉트
        }

        // 5. 실제 데이터 조회 (유효한 페이지 번호를 사용하여 데이터 가져오기)
        List<Tuple> results = query
                .offset((long) currentPage * pageable.getPageSize()) // offset은 (currentPage * pageSize)
                .limit(pageable.getPageSize()) // 페이지 사이즈 만큼 데이터 가져오기
                .fetch(); // 실제 데이터 조회


        List<PostSummaryResponseDTO> posts = results.stream()
                .map(tuple -> {
                    Post postEntity = tuple.get(post);
                    long commentCount = Optional.ofNullable(tuple.get(comment.count())).orElse(0L);

                    PostSummaryResponseDTO postResponseDTO = new PostSummaryResponseDTO();
                    postResponseDTO.setPostId(postEntity.getPostId());
                    postResponseDTO.setTitle(postEntity.getTitle());
                    postResponseDTO.setCategory(postEntity.getCategory().getLabel());
                    postResponseDTO.setCreateAt(postEntity.getCreateAt().toString());
                    postResponseDTO.setNickname(postEntity.getMember().getNickname());
                    postResponseDTO.setCommentCount(commentCount);
                    postResponseDTO.setViewCount(postEntity.getViewCount());

                    return postResponseDTO;
                })
                .collect(Collectors.toList());



        PostPageResponseDTO<PostSummaryResponseDTO> postPageResponseDTO = new PostPageResponseDTO<>(
                posts,
                totalPages,
                totalElements,
                currentPage + 1,
                pageable.getPageSize()
        );

        return postPageResponseDTO;



    }

    private JPAQuery<Tuple> getQuery(QPost post, QComment comment) {
        JPAQuery<Tuple> query = queryFactory.select(post, comment.count())
                .from(post)
                .leftJoin(post.comments, comment)
                .groupBy(post.postId);
        return query;
    }
}
