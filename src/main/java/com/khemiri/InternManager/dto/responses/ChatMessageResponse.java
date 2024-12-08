package com.khemiri.InternManager.dto.responses;


import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ChatMessageResponse {
    private String contenu;
    private String dateEnvoi;
    private Long expediteurId;
    private Long destinataireId;
}
