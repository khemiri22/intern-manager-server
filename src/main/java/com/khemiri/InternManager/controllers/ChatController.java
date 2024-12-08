package com.khemiri.InternManager.controllers;


import com.khemiri.InternManager.dto.requests.ChatMessageRequest;
import com.khemiri.InternManager.dto.responses.ChatMessageResponse;
import com.khemiri.InternManager.dto.responses.ChatRoomResponse;
import com.khemiri.InternManager.dto.responses.TokenPayloadResponse;
import com.khemiri.InternManager.entities.Admin;
import com.khemiri.InternManager.entities.ChatMessage;
import com.khemiri.InternManager.entities.Stagiaire;
import com.khemiri.InternManager.entities.Utilisateur;
import com.khemiri.InternManager.services.admin.IServiceAdmin;
import com.khemiri.InternManager.services.chatMessage.IServiceChatMessage;
import com.khemiri.InternManager.services.stagiaire.IServiceStagiaire;
import com.khemiri.InternManager.services.utilisateur.IServiceUtilisateur;
import com.khemiri.InternManager.utils.CookiesManager;
import com.khemiri.InternManager.utils.JwtManager;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/chat")
@AllArgsConstructor
public class ChatController {
    private final IServiceChatMessage serviceChatMessage;
    private final IServiceUtilisateur serviceUtilisateur;
    private final IServiceStagiaire serviceStagiaire;
    private final IServiceAdmin serviceAdmin;
    private SimpMessagingTemplate simpMessagingTemplate;
    private final JwtManager jwtManager;


    @MessageMapping("/private-message")
    // -> destination : /app/private-message
    // -> queue : /user/userId/private
    public ChatMessageResponse receivePrivateMessage(@Payload ChatMessageRequest messageRequest, StompHeaderAccessor accessor) throws Exception {
        System.out.println(messageRequest   );
        String token = (String) accessor.getSessionAttributes().get("token");
        if(token == null || !isValidToken(token))
            simpMessagingTemplate.convertAndSendToUser(messageRequest.getExpediteurId().toString(),"/error","Accès refusé !");

        Utilisateur expediteur = serviceUtilisateur.findById(messageRequest.getExpediteurId()).orElse(null);
        Utilisateur destinataire = serviceUtilisateur.findById(messageRequest.getDestinataireId()).orElse(null);
        if(expediteur == null)
            simpMessagingTemplate.convertAndSendToUser(messageRequest.getExpediteurId().toString(),"/error","Expediteur non trouvé !");
        if(destinataire == null)
            simpMessagingTemplate.convertAndSendToUser(messageRequest.getExpediteurId().toString(),"/error","Destinataire non trouvé !");
        ChatMessageResponse messageResponse = ChatMessageResponse.builder()
                .contenu(messageRequest.getContenu())
                .destinataireId(messageRequest.getDestinataireId())
                .expediteurId(messageRequest.getExpediteurId())
                .dateEnvoi(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()))
                .build();
        serviceChatMessage.saveChatMessage(
                ChatMessage.builder()
                        .contenu(messageResponse.getContenu())
                        .dateEnvoi(new Date())
                        .expediteur(expediteur)
                        .destinataire(destinataire)
                        .build()
        );
        simpMessagingTemplate.convertAndSendToUser(messageRequest.getDestinataireId().toString(),"/private",messageResponse);
        return messageResponse;
    }
        private boolean isValidToken(String token) {
            TokenPayloadResponse payloadResponse = jwtManager.validateJwtToken(token);
            return payloadResponse != null;
        }
    @GetMapping("/getInfoForInternToChat")
    public ResponseEntity<Object> getInfoForInternToChat(@CookieValue(name = "token", required = false)String cookieValue)
    {
        String token = CookiesManager.getCookie(cookieValue);
        if (token == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        TokenPayloadResponse tokenPayloadResponse = jwtManager.validateJwtToken(token);
        if(tokenPayloadResponse == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        if(!tokenPayloadResponse.getRole().equals("intern"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        Stagiaire stagiaire = serviceStagiaire.findById(Long.parseLong(tokenPayloadResponse.getId())).orElse(null);
        if (stagiaire == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Stagiaire non trouvé !");
        return ResponseEntity.ok().body(
                new Object(){
                    public final Long id = stagiaire.getId();
                    public final String fullName = stagiaire.getPrenom() + " " +stagiaire.getPrenom();
                    public final Long adminId = stagiaire.getAdmin().getId();
                }
        );
    }
    @GetMapping("/getMessagesOfUser")
    public ResponseEntity<Object> getMessagesOfInternWithAdmin(@CookieValue(name = "token", required = false)String cookieValue)
    {
        String token = CookiesManager.getCookie(cookieValue);
        if (token == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        TokenPayloadResponse tokenPayloadResponse = jwtManager.validateJwtToken(token);
        if(tokenPayloadResponse == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        Utilisateur user = serviceUtilisateur.findById(Long.parseLong(tokenPayloadResponse.getId())).orElse(null);
        if (user == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé !");
        return ResponseEntity.ok().body(serviceChatMessage.getAllChatsMessagesForUser(user.getId()));

    }

    @GetMapping("/getInfoForAdminToChat")
    public ResponseEntity<Object> getInfoForAdminToChat(@CookieValue(name = "token", required = false)String cookieValue)
    {
        String token = CookiesManager.getCookie(cookieValue);
        if (token == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        TokenPayloadResponse tokenPayloadResponse = jwtManager.validateJwtToken(token);
        if(tokenPayloadResponse == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        if(!tokenPayloadResponse.getRole().equals("admin"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        Admin admin = serviceAdmin.findById(Long.parseLong(tokenPayloadResponse.getId())).orElse(null);
        if (admin == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("admin non trouvé !");
        return ResponseEntity.ok().body(
                new Object(){
                    public final Long id = admin.getId();
                }
        );
    }


}
