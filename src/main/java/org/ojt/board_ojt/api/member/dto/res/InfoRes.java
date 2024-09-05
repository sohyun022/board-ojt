package org.ojt.board_ojt.api.member.dto.res;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;
import org.ojt.board_ojt.api.member.domain.Job;
import org.ojt.board_ojt.api.member.domain.Role;

@Builder
//requiredConstructor 와 함께 사용할 수 없음
//@RequiredArgsConstructor는 final 필드나 @NonNull이 붙은 필드에 대해 생성자를 자동으로 생성합니다.
// 반면, @Builder는 모든 필드를 포함하는 빌더 클래스와 빌더 메서드를 생성하려고 합니다.
// 이 둘이 충돌하면서 컴파일 오류가 발생한 것입니다.
@ToString
@AllArgsConstructor
public class InfoRes {

    private String email;
    private String name;
    private Job job;
    private String introduction;
    private String profileImageUrl;
    private Role role;

}
