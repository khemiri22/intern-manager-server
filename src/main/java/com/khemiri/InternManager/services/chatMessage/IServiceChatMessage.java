package com.khemiri.InternManager.services.chatMessage;

import com.khemiri.InternManager.dto.responses.ChatRoomResponse;
import com.khemiri.InternManager.entities.ChatMessage;
import com.khemiri.InternManager.entities.Utilisateur;

import java.util.List;

public interface IServiceChatMessage {
    ChatMessage saveChatMessage(ChatMessage chatMessage);
    List<ChatRoomResponse> getAllChatsMessagesForUser(Long userId);
}
