package org.ojt.board_ojt.api.auth.dto.res;

import lombok.Builder;
import lombok.Getter;

@Getter //왜 필요한가
@Builder
public class TokenRes {

    //private String grantType;
    //이 필드는 토큰의 인증 타입을 나타냅니다. 일반적으로 JWT를 사용할 때 "Bearer" 타입을 사용하며,
    // 이 값은 클라이언트가 서버에 요청할 때 HTTP 헤더의 Authorization 필드에 붙여지는 프리픽스(prefix)가 됩니다.
    //왜 Bearer 타입을 사용하나요?
    //Bearer 타입을 사용하는 이유는 간단한 인증 메커니즘을 제공하기 위해서입니다:
    //간편한 사용: Bearer 토큰은 간단하게 서버가 발급한 토큰을 헤더에 추가하는 것으로 인증을 처리할 수 있어 사용이 매우 쉽습니다.
    //표준화: Bearer는 HTTP 기반 인증을 위한 표준 방식으로, 대부분의 API가 이 방식을 채택하고 있습니다.
    //무상태 인증: 서버가 클라이언트의 인증 상태를 저장할 필요 없이, 각 요청에서 토큰만으로 인증을 처리할 수 있습니다.
    private final Long memberId;
    private final String accessToken;
    private final String refreshToken;

//    public enum TokenType{
//        REFRESH,ACCESS
//    }

}
