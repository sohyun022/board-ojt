package org.ojt.board_ojt.api.member.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.ojt.board_ojt.api.board.domain.Post;
import org.ojt.board_ojt.api.member.dto.req.PatchInfoReq;

import java.util.List;

@Entity
@Getter
@RequiredArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Email //필드가 이메일 형식인지 확인
    @NotBlank
    @Column(nullable = false, unique = true)//회원가입 후 변경 불가
    private String email;

    @NotBlank
    private String name; //회원가입 후 변경 가능

    @ToString.Exclude
    @JsonIgnore
    @JsonProperty
    @NotBlank
    private String password;

    @Enumerated(EnumType.STRING)
    private Job job; // 직무: 백엔드, 프론트, 기획

    private String profileImageUrl; // 프로필 이미지 경로

    private String introduction; // 자기소개

    @Enumerated(EnumType.STRING)
    private Role role; // 권한: ADMIN, USER

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;

    // mappedBy 속성은 관계의 주인이 아님을 명시합니다. 즉, Member 엔티티는 외래 키를 소유하지 않으며
    // Post 엔티티에 의해 관리되는 관계임을 의미합니다.

    @Builder
    public Member(String email, String password, String name, Job job, String profileImageUrl, String introduction,Role role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.job = job;
        this.role = role;
        this.profileImageUrl = profileImageUrl;
        this.introduction = introduction;
    }

    public void privatePatchInfo(PatchInfoReq patchInfoReq){ //update 전용 dto 를 매개변수로 줌
        if (patchInfoReq.getName() != null) {
            this.name = patchInfoReq.getName();
        }
        if (patchInfoReq.getJob() != null) {
            this.job = patchInfoReq.getJob();
        }
        if (patchInfoReq.getProfileImageUrl() != null) {
            this.profileImageUrl = patchInfoReq.getProfileImageUrl();
        }
        if (patchInfoReq.getIntroduction() != null) {
            this.introduction = patchInfoReq.getIntroduction();
        }
    } //왜 이 메서드가 여기에 위치하는지 모르겠다 -> setter 를 사용할 수 없으니 엔티티 클래스에 메서드를 만들어서 사용?


}



