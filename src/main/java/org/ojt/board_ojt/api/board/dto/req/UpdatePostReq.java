package org.ojt.board_ojt.api.board.dto.req;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.ojt.board_ojt.api.board.domain.BoardType;

@Getter
@ToString
@RequiredArgsConstructor
public class UpdatePostReq {

    private String title;

    private BoardType boardType;

    private String content;

    private String postImage;
}
