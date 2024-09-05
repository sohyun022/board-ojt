package org.ojt.board_ojt.api.comment.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ojt.board_ojt.api.member.domain.Member;
import org.ojt.board_ojt.api.board.domain.Post;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "post_comment")
public class Comment {

    @Id
    private Long commentId;

    @ManyToOne
    @JoinColumn(name="commen_id")
    private Comment parentCommentId;

    private Long depth;

    @ManyToOne(fetch = FetchType.LAZY)  // 게시글과 다대일 관계 설정
    @JoinColumn(name = "post_id", nullable = false)  // 외래 키 설정
    private Post post;  // `Post` 엔티티와의 관계를 설정하는 필드

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member author;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void updateComment(String content) {
        this.content=content;
    }

}
