package com.khemiri.InternManager.dto.requests;


import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ChatMessageRequest {
    private String contenu;
    private Long expediteurId;
    private Long destinataireId;
}
