package com.khemiri.InternManager.dto.responses;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ChatRoomResponse {
    private List<ChatMessageResponse> chatRoomMessages;
    private UserResponse destinataire;
}
