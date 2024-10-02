package org.ojt.board_ojt.api.board.domain;

import jakarta.persistence.*;

import lombok.*;

import org.ojt.board_ojt.api.comment.domain.Comment;
import org.ojt.board_ojt.api.member.domain.Member;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Entity
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor//객체를 생성할 때 아무런 초기화 값 없이 기본 생성자를 호출할 수 있어야 하는 경우에 사용됩니다. 예를 들어, JPA 엔티티나 스프링 프레임워크에서 빈(bean)을 생성할 때 사용됩니다.
//@RequiredArgsConstructor: final 필드와 @NonNull이 붙은 필드를 포함하는 생성자를 자동으로 생성합니다.

@Builder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class) // Auditing 기능 활성화
@Table(name="post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member author;

    private String title;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private BoardType boardType;

    // 댓글 목록
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    private String postImage;

    @Column(nullable = false)
    @Setter
    private Long likes;

    @Column(nullable = false)
    @Setter
    private Long views;

    @Column(nullable = false)
    private Long comments;

    private boolean delYn; // 게시글 삭제 여부

    //fetchtype.eager 연관 된 엔티티가 즉시 로딩됨
    //cascadetype.reomove 부모 엔티티가 삭제될 때 자식 엔티티도 함께 삭제
    //eager를 사용할 경우데이터가 많을 경우 성능 문제가 발생할 수 있으므로, FetchType.LAZY를 사용하는 것이 더 일반적이다
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @OrderBy("createdAt asc")
    private List<Comment> commentList;

    public void delete(){

        this.delYn = true;
    }

    public void restore() {

        this.delYn = false;
    }

    // 엔티티가 저장되기 전에 실행
    @PrePersist
    public void onPrePersist() {
        this.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        this.updatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
    }

    // 엔티티가 업데이트되기 전에 실행
    @PreUpdate
    public void onPreUpdate() {
        this.updatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
    }


}
