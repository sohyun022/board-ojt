package org.ojt.board_ojt.security;

import lombok.RequiredArgsConstructor;
import org.ojt.board_ojt.api.member.repository.MemberRepository;
import org.ojt.board_ojt.api.member.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // 사용자 ID로 사용자 정보 조회
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

        // UserDetails로 변환
        return new org.springframework.security.core.userdetails.User(member.getName(), member.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));

        //모든 회원가입자는 user가 되나
        //만약 여기서 일괄적으로 유저로 등록하고 멤버 엔티티에 롤 컬럼에 어떻게 유저로 저장할까

    }

}
