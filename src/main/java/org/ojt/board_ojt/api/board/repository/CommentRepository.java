package org.ojt.board_ojt.api.board.repository;

import org.ojt.board_ojt.api.board.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Comment findByCommentId(Long commentId);

}
