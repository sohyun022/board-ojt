package org.ojt.board_ojt.security;

import lombok.RequiredArgsConstructor;
import org.ojt.board_ojt.api.member.repository.MemberRepository;
import org.ojt.board_ojt.api.member.domain.Member;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

        return new CustomUserDetails(member);
    }

    //모든 회원가입자는 user 권한을 부여해야 되나
    //만약 여기서 일괄적으로 유저로 등록하면 멤버 엔티티에 롤 컬럼에 어떻게 유저로 저장할까


}
