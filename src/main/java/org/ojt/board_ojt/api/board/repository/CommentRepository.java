package org.ojt.board_ojt.api.board.repository;

import org.ojt.board_ojt.api.board.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Comment findByCommentId(Long commentId);

    @Modifying
    @Query("UPDATE Comment e SET e.content = :content, e.updatedAt = :updatedAt WHERE e.commentId = :id")
    void updateComment(@Param("id") Long id, @Param("content") String content, @Param("updatedAt") LocalDateTime updatedAt);

}
