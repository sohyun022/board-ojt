package org.ojt.board_ojt.api.member.service;

import lombok.RequiredArgsConstructor;
import org.ojt.board_ojt.api.member.dto.req.JoinReq;
import org.ojt.board_ojt.api.member.dto.req.PatchInfoReq;
import org.ojt.board_ojt.api.member.dto.res.InfoRes;
import org.ojt.board_ojt.api.member.domain.Member;
import org.ojt.board_ojt.api.member.repository.MemberRepository;
import org.ojt.board_ojt.security.CustomUserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
    //회원가입, 정보 수정, 마이페이지 조회, 중복 아이디 조회

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Member joinMember(JoinReq joinReq){

        checkEmailDuplication(joinReq.getEmail()); // try-catch 를 안 써도 되나

        String encodedPassword = passwordEncoder.encode(joinReq.getPassword());

        Member member= Member.builder()
                .email(joinReq.getEmail())
                .password(encodedPassword)
                .name(joinReq.getName())
                .job(joinReq.getJob())
                .introduction(joinReq.getIntroduction())
                .role(joinReq.getRole())
                .build();

        if(joinReq.getProfileImageUrl()==null){
            Member.builder()
                    .profileImageUrl("https://www.studiopeople.kr/common/img/default_profile.png")
                    .build();

        }

        else{
            Member.builder()
                    .profileImageUrl(joinReq.getProfileImageUrl())
                    .build();
        }

        return memberRepository.save(member);

    }

    @Override
    public InfoRes getMemberInfo(Long id){
        //id 대신 유저 디테일이 들어가야함
        //Member member = userDetail.getMember();
        //return InfoRes.builder()

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다: " + id));

        return InfoRes.builder()
                .email(member.getEmail())
                .name(member.getName())
                .job(member.getJob())
                .profileImageUrl(member.getProfileImageUrl())
                .introduction(member.getIntroduction())
                .role(member.getRole())
                .build();

    }

    @Override
    @Transactional
    public void patchInfo(PatchInfoReq patchInfoReq, CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        member.privatePatchInfo(patchInfoReq);

        member.privatePatchInfo(patchInfoReq);

        memberRepository.save(member);
    }

    // optional 을 써야하나? 에러를 던져야하나?
    // 매개변수를 인스턴스 말고 아이디만 넣어서 해줄 수는 없나??
    // 예외가 던져지면 어떻게 됨 -> error 500
    // 에러 메세지 응답 메세지에 어떻게 담지

    @Override
    public void checkEmailDuplication(String email){
        boolean exists = memberRepository.existsByEmail(email);
        if (exists) {
            throw new RuntimeException("이미 존재하는 이메일입니다."); // 사용자 정의 예외 클래스로 교체 가능
        }
    }


}