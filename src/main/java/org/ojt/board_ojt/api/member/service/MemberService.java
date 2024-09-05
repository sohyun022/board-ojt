package org.ojt.board_ojt.api.member.service;

import org.ojt.board_ojt.api.member.domain.Member;
import org.ojt.board_ojt.api.member.dto.req.JoinReq;
import org.ojt.board_ojt.api.member.dto.req.PatchInfoReq;
import org.ojt.board_ojt.api.member.dto.res.InfoRes;
import org.ojt.board_ojt.security.CustomUserDetails;

public interface MemberService {

    Member joinMember(JoinReq joinReq);

    InfoRes getMemberInfo(Long id); //id 대신 user detail

    void patchInfo(PatchInfoReq patchInfoReq, CustomUserDetails userDetails);

    void checkEmailDuplication(String email);


}
