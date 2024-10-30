package org.ojt.board_ojt.api.board.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class CommentReq {

    private Long parentCommentId;

    @NotBlank(message = "댓글 내용은 필수 입력 항목입니다.")
    private String content;

}
