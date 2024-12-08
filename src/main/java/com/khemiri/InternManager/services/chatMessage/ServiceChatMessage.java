package com.khemiri.InternManager.services.chatMessage;

import com.khemiri.InternManager.dto.responses.ChatMessageResponse;
import com.khemiri.InternManager.dto.responses.ChatRoomResponse;
import com.khemiri.InternManager.dto.responses.UserResponse;
import com.khemiri.InternManager.entities.ChatMessage;
import com.khemiri.InternManager.repositories.ChatMessageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ServiceChatMessage implements IServiceChatMessage {
    private ChatMessageRepository chatMessageRepository;
    public ChatMessage saveChatMessage(ChatMessage chatMessage) {
        return chatMessageRepository.save(chatMessage);
    }
    public List<ChatRoomResponse> getAllChatsMessagesForUser(Long userId) {
        List<ChatMessage> messages = chatMessageRepository.findByUserId(userId);
        return messages.stream()
                .collect(Collectors.groupingBy(cm -> {
                    if (cm.getExpediteur().getId().equals(userId)) {
                        return cm.getDestinataire();
                    } else {
                        return cm.getExpediteur();
                    }
                }))
                .entrySet().stream()
                .map(entry -> ChatRoomResponse.builder()
                        .destinataire(
                                UserResponse.builder()
                                        .id(entry.getKey().getId())
                                        .nom(entry.getKey().getNom())
                                        .prenom(entry.getKey().getPrenom())
                                        .email(entry.getKey().getEmail())
                                        .role(entry.getKey().getRole())
                                        .build()
                        )
                        .chatRoomMessages(
                                entry.getValue().stream().map(
                                        chatMessage -> ChatMessageResponse.builder()
                                                .expediteurId(chatMessage.getExpediteur().getId())
                                                .destinataireId(chatMessage.getDestinataire().getId())
                                                .contenu(chatMessage.getContenu())
                                                .dateEnvoi(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(chatMessage.getDateEnvoi()))
                                                .build()
                                ).toList()
                        ).build()
                )
                .collect(Collectors.toList());
    }
    }

