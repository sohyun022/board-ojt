package org.ojt.board_ojt.api.comment.repository;

import org.ojt.board_ojt.api.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
