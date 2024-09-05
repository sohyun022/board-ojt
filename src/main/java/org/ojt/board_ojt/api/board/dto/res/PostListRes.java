package org.ojt.board_ojt.api.board.dto.res;

import lombok.*;
import org.ojt.board_ojt.api.board.domain.BoardType;
import org.ojt.board_ojt.api.comment.domain.Comment;
import org.ojt.board_ojt.api.board.domain.Like;
import org.ojt.board_ojt.api.board.domain.View;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class PostListRes {

    private Long author;

    private String title;

    private Long views; // 댓글 수만 알려주면 되는데 객체를 통째로 넘겨주면 너무 무겁나??
    private Long likes;
    private Long comments;

    private LocalDateTime createdAt;

    private String boardType;

}
