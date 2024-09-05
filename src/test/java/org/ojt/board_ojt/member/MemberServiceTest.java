package org.ojt.board_ojt.member;


import org.junit.jupiter.api.Test;
import org.ojt.board_ojt.BoardOjtApplication;
import org.ojt.board_ojt.api.member.domain.Job;
import org.ojt.board_ojt.api.member.domain.Member;
import org.ojt.board_ojt.api.member.domain.Role;
import org.ojt.board_ojt.api.member.dto.req.JoinReq;

import org.ojt.board_ojt.api.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(classes = BoardOjtApplication.class)
public class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Test
    void testJoin(){

        JoinReq joinReq = new JoinReq();
        joinReq.setEmail("sohyun2239@abc.kr");
        joinReq.setPassword("123456");
        joinReq.setJob(Job.BACKEND);
        joinReq.setRole(Role.ADMIN);
        joinReq.setIntroduction("hello i'm 23 years old");
        joinReq.setName("문소현");

        Member member= memberService.joinMember(joinReq);

        assertNotNull(member);
        assertEquals(joinReq.getEmail(), member.getEmail());
        System.out.println(member);
    }

}
