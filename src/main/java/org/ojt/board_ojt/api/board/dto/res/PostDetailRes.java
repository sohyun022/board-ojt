package org.ojt.board_ojt.api.board.dto.res;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.ojt.board_ojt.api.board.domain.BoardType;
import org.ojt.board_ojt.api.comment.domain.Comment;
import org.ojt.board_ojt.api.board.domain.Like;
import org.ojt.board_ojt.api.board.domain.View;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
@RequiredArgsConstructor
public class PostDetailRes {

    private String author;

    private String title;

    private String content;

    private String image;

    private List<View> views;

    private LocalDateTime createdAt;

    private BoardType board;

    private List<Comment> comments;

    private List<Like> likes;

}
