package org.ojt.board_ojt.api.auth.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@DynamicUpdate
@Getter
@NoArgsConstructor
@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refreshTokenId;
    private String refreshToken;
    private String email;

    @Builder
    public RefreshToken(String token, String email) {
        this.refreshToken = token;
        this.email = email;
    }

    public void updateToken (String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
