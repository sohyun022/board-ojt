package org.ojt.board_ojt.api.board.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.ojt.board_ojt.api.member.domain.Member;

@Entity
@Getter
@Table(name = "post_like")
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId; // 좋아요 id

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post postId;  // 좋아요가 눌린 게시글

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member memberId;  // 좋아요를 누른 사용자


}
