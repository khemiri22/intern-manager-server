package com.khemiri.InternManager.dto.responses;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class TokenPayloadResponse {
    private String email;
    private String role;
    private String id;
}
