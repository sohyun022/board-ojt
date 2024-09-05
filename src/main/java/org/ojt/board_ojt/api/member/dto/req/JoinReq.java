package org.ojt.board_ojt.api.member.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.*;
import org.ojt.board_ojt.api.member.domain.Job;
import org.ojt.board_ojt.api.member.domain.Role;


@Getter
@ToString
@Setter
@RequiredArgsConstructor
public class JoinReq {

    @Email
    @NotBlank(message = "아이디(이메일)을 입력하세요.")
    private String email;

    @NotBlank(message = "이름을 입력하세요.")
    private String name;

    @ToString.Exclude
    @NotBlank(message = "비밀번호를 입력하세요.")
    private String password;

    private Job job;
    private String introduction;
    private String profileImageUrl;
    private Role role;



}
