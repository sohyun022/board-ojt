package org.ojt.board_ojt.api.board.dto.req;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UpdateCommentReq {

    String content;
}
