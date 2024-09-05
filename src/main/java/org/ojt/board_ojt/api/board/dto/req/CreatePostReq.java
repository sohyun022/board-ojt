package org.ojt.board_ojt.api.board.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.ojt.board_ojt.api.board.domain.BoardType;

@Getter
@ToString
@AllArgsConstructor
public class CreatePostReq {

    @NotBlank
    private String title;

    @NotBlank
    private BoardType boardType;

    @NotBlank
    private String content;

    private String postImage;

}
