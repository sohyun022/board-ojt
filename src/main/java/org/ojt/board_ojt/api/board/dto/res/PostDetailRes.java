package org.ojt.board_ojt.api.board.dto.res;

import lombok.*;
import org.ojt.board_ojt.api.board.domain.BoardType;
import org.ojt.board_ojt.api.board.domain.Comment;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
@Builder
public class PostDetailRes {

    private Long author;

    private String title;

    private String content;

    private String image;

    private Long views;

    private Long likes;

    private Long comments;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private BoardType boardType;

    private List<Comment> commentsList;

}
