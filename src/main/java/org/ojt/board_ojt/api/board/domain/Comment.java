package org.ojt.board_ojt.api.board.domain;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ojt.board_ojt.api.member.domain.Job;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post_comment")
@EntityListeners(AuditingEntityListener.class) // Auditing 기능 활성화
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")  // 부모 댓글 ID
    private Comment parentComment;  // 부모 댓글

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> childComments = new ArrayList<>();  // 자식 댓글들

    @ManyToOne(fetch = FetchType.LAZY)  // 게시글과 다대일 관계 설정
    @JoinColumn(name = "post_id", nullable = false)  // 외래 키 설정
    private Post post;  // `Post` 엔티티와의 관계를 설정하는 필드

    private Long writerId;

    private String writerProfileImage;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    boolean isDeleted = false;

    private Long childCnt = 0L; //덧글 수

    private Job writerJob;

    public void updateComment(String content) {
        this.content=content;
    }

    public void deleteComment() {
        this.isDeleted = true;
    }

    // 자식 댓글 추가 메서드
    public void addChildComment(Comment childComment) {
        childComments.add(childComment);
        childComment.setParentComment(this);
    }

    // 엔티티가 저장되기 전에 실행
    public void onPrePersist() {
        this.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        this.updatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
    }

    // 덧글 수 증가 메서드
    public void incrementReplyCountWithoutUpdatingModifiedDate() {
        this.childCnt += 1;
        // `updatedAt`이 자동으로 갱신되지 않도록 이 메서드에서는 `@LastModifiedDate` 적용을 피함
    }


}
