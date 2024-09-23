package org.ojt.board_ojt.api.board.domain;

import jakarta.persistence.*;
import lombok.*;
import org.ojt.board_ojt.api.member.domain.Member;
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

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;  // 조회된 게시글

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;  // 조회한 사용자

    @CreatedDate
    private LocalDateTime createdAt; // 조회 일자

    public View(Post post, Member member, LocalDateTime createdAt) {
        this.post = post;
        this.member = member;
        this.createdAt = createdAt;
    }

}
