package org.ojt.board_ojt.api.auth.dto.req;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@RequiredArgsConstructor

public class LoginReq {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
