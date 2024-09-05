package org.ojt.board_ojt.api.board.dto.req;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.ojt.board_ojt.api.board.domain.SortType;

import java.time.LocalDateTime;

@Getter
@ToString
@AllArgsConstructor
@Builder
public class PostListReq {

    @Builder.Default
    @Enumerated(EnumType.STRING)
    SortType sortType=SortType.createdAt;
    int page;
    Long author;
    String title;
    LocalDateTime startDt;
    LocalDateTime endDt;
}
