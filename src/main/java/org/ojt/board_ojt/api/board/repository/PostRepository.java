package org.ojt.board_ojt.api.board.repository;

import org.ojt.board_ojt.api.board.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    public Long findByPostId(Long postId);
    @Query("SELECT p FROM Post p WHERE " +
            "(:author IS NULL OR p.author.memberId = :author) AND " +
            "(:title IS NULL OR p.title LIKE %:title%) AND " +
            "(:startDt IS NULL OR p.createdAt >= :startDt) AND " +
            "(:endDt IS NULL OR p.createdAt <= :endDt)")

    Page<Post> findByFilters(
            @Param("author") Long author,
            @Param("title") String title,
            @Param("startDt") LocalDateTime startDt,
            @Param("endDt") LocalDateTime endDt,
            Pageable pageable);
}
