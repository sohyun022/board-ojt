package org.ojt.board_ojt.api.board.domain;

import jakarta.persistence.*;
import lombok.*;

import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "post_view")
@NoArgsConstructor
public class View {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long viewId; // 조회 id

    private Long postId;  // 조회된 게시글

    private Long memberId;  // 조회한 사용자

    @CreatedDate
    private LocalDateTime createdAt; // 조회 일자

    public View(Long postId, Long memberId, LocalDateTime createdAt) {
        this.postId = postId;
        this.memberId = memberId;
        this.createdAt = createdAt;
    }

}
