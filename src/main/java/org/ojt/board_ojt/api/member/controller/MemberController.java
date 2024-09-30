package org.ojt.board_ojt.api.member.controller;

import lombok.RequiredArgsConstructor;
import org.ojt.board_ojt.api.member.dto.req.JoinReq;
import org.ojt.board_ojt.api.member.dto.req.PatchInfoReq;
import org.ojt.board_ojt.api.member.dto.res.InfoRes;
import org.ojt.board_ojt.api.member.domain.Member;
import org.ojt.board_ojt.api.member.repository.MemberRepository;
import org.ojt.board_ojt.api.member.service.MemberService;

import org.ojt.board_ojt.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/member")
public class MemberController { // 마이페이지, 정보수정, 회원가입

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @GetMapping("/")
    public ResponseEntity<?> viewMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        InfoRes infoRes= memberService.getMemberInfo(userDetails.getMember().getMemberId());

        String message = "회원 정보 조회 완료!\n"
                + "viewed data: " + infoRes;

        return ResponseEntity.ok(message);
    }

    @PatchMapping("/")
    public ResponseEntity<?> patchMyInfo(@RequestBody PatchInfoReq patchInfoReq,  @AuthenticationPrincipal CustomUserDetails userDetails) {
        memberService.patchInfo(patchInfoReq,userDetails);

        Optional<Member> member = memberRepository.findById(userDetails.getMember().getMemberId());

        String message;

        message = member.map(value -> value.getName() + "님 회원 정보 수정 완료!\n"
                + "patched data: " + patchInfoReq).orElse("회원 정보 수정 실패 (존재하지 않는 회원)");

        return ResponseEntity.ok(message);

    }

    @PostMapping("/join")
    public ResponseEntity<?> joinMember(@RequestBody JoinReq joinReq) {


        Member member = memberService.joinMember(joinReq);

        String message = member.getName() + "님 회원 가입 완료!\n"
                + "received data: " + joinReq;

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(message);

    }

}
