package org.ojt.board_ojt.api.board.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.ojt.board_ojt.api.member.domain.Member;

@Entity
@Getter
@Table(name = "post_view")
public class View {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long viewId; // 조회 id

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;  // 조회된 게시글

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = true)  // 조회한 사용자 (비회원일 경우 null)
    private Member member;  // 조회한 사용자
}
