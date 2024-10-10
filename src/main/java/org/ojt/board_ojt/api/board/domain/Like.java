package org.ojt.board_ojt.api.board.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "post_like")
@NoArgsConstructor
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId; // 좋아요 id

    private Long postId;  // 좋아요 한 게시글

    private Long memberId;  // 좋아요 한 사용자

    public Like(Long postId, Long memberId) {
        this.postId = postId;
        this.memberId = memberId;
    }

}
