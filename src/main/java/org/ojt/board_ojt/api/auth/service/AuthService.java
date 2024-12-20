package org.ojt.board_ojt.api.auth.service;

import org.ojt.board_ojt.api.auth.domain.RefreshToken;
import org.ojt.board_ojt.api.auth.dto.req.LoginReq;
import org.ojt.board_ojt.api.auth.dto.res.TokenRes;
import org.ojt.board_ojt.api.member.domain.Member;

public interface AuthService {

    TokenRes login(LoginReq loginReq);
    RefreshToken insertRefreshToken(Member member);
    String reissuedAccessToken (String refreshToken);
}
