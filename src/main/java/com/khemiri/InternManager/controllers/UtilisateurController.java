package com.khemiri.InternManager.controllers;


import com.khemiri.InternManager.dto.responses.TokenPayloadResponse;
import com.khemiri.InternManager.dto.requests.UserLoginRequest;
import com.khemiri.InternManager.dto.responses.UserResponse;
import com.khemiri.InternManager.entities.Stagiaire;
import com.khemiri.InternManager.entities.Utilisateur;
import com.khemiri.InternManager.services.stagiaire.IServiceStagiaire;
import com.khemiri.InternManager.services.utilisateur.IServiceUtilisateur;
import com.khemiri.InternManager.utils.CookiesManager;
import com.khemiri.InternManager.utils.JwtManager;
import com.khemiri.InternManager.utils.PasswordManager;
import com.khemiri.InternManager.utils.RandomString;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UtilisateurController {
    private IServiceUtilisateur serviceUtilisateur;
    private IServiceStagiaire serviceStagiaire;
    private JwtManager jwtManager;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody UserLoginRequest userLoginRequest, HttpServletResponse response) {
        Utilisateur user = serviceUtilisateur.findByEmail(userLoginRequest.getEmail()).orElse(null);
        if(user == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Informations d'identification erronées!");
        else {
            if(!PasswordManager.checkPassword(userLoginRequest.getMotDePasse(),user.getMotDePasse()))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Informations d'identification erronées!");
            CookiesManager.setCookie(response,"token", jwtManager.generateJwtToken(user.getEmail(),user.getRole(),user.getId().toString()),true);
            serviceUtilisateur.connect(user);
            String sessionToken = RandomString.generatePassword(20,user.getId().toString());
            return ResponseEntity.ok().body(new UserResponse(
                    user.getId(),user.getNom(),user.getPrenom(),user.getEmail(),user.getRole()
            ));
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<Object> logout(HttpServletRequest request, HttpServletResponse response,@CookieValue(name = "token", required = false)String cookieValueToken,@CookieValue(name = "token", required = false)String cookieValueSession){
        String token = CookiesManager.getCookie(cookieValueToken);
        TokenPayloadResponse tokenPayloadResponse = jwtManager.validateJwtToken(token);
        if(tokenPayloadResponse == null)
            return ResponseEntity.ok("Informations d'identification erronées!");
        Utilisateur utilisateur = serviceUtilisateur.findByEmail(tokenPayloadResponse.getEmail()).orElse(null);
        if (utilisateur == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("utilisateur non trouvé !");
        serviceUtilisateur.disconnect(utilisateur);
        CookiesManager.deleteCookie(request,response,"token",true);
        return ResponseEntity.ok().body(new UserResponse(
                utilisateur.getId(),utilisateur.getNom(),utilisateur.getPrenom(),utilisateur.getEmail(),utilisateur.getRole()
        ));
    }
    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id,@CookieValue(name = "token", required = false)String cookieValue) {
        String token = CookiesManager.getCookie(cookieValue);
        if (token == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        TokenPayloadResponse tokenPayloadResponse = jwtManager.validateJwtToken(token);
        if (tokenPayloadResponse == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        Utilisateur user =  serviceUtilisateur.findById(id).orElse(null);
        if (user == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé !");
        return ResponseEntity.ok().body(
                UserResponse
                        .builder()
                        .id(user.getId())
                        .nom(user.getNom())
                        .prenom(user.getPrenom())
                        .role(user.getRole())
                        .email(user.getEmail())
                        .build()
        );

    }
    @GetMapping
    public List<Utilisateur> getAllUsers() {
        return serviceUtilisateur.findAll();
    }
    @GetMapping("/getAllInternsToChat")
    public ResponseEntity<Object> getAllInternsToChat(@CookieValue(name = "token", required = false)String cookieValue) {
        String token = CookiesManager.getCookie(cookieValue);
        if (token == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        TokenPayloadResponse tokenPayloadResponse = jwtManager.validateJwtToken(token);
        if (tokenPayloadResponse == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        if(!tokenPayloadResponse.getRole().equals("admin"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        Utilisateur user = serviceUtilisateur.findById(Long.parseLong(tokenPayloadResponse.getId())).orElse(null);
        if (user == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé !");
        List<UserResponse> internList = new ArrayList<>();
        serviceStagiaire.findAll().stream()
                .filter(stagiaire -> stagiaire.getAdmin().getId() == Long.parseLong(tokenPayloadResponse.getId()))
                .forEach(stagiaire -> {
                    internList.add(UserResponse.builder()
                            .id(stagiaire.getId())
                            .nom(stagiaire.getNom())
                            .prenom(stagiaire.getPrenom())
                            .role(stagiaire.getRole())
                            .email(stagiaire.getEmail())
                            .build());
                });
        return ResponseEntity.ok().body(internList);

    }
    @GetMapping("/connectedUsers")
    public ResponseEntity<Object> findConnectedUsers()
    {
        return ResponseEntity.ok().body(serviceUtilisateur.findConnectedUsers());
    }
    @GetMapping("/verifyToken")
    public TokenPayloadResponse verifyIfConnected(@CookieValue(name = "token", required = false)String cookieValue)
    {
        String token = CookiesManager.getCookie(cookieValue);
        if (token == null)
            return null;
        return jwtManager.validateJwtToken(token);
    }
    @GetMapping("/sendRecoverMail/{email}")
    public ResponseEntity<Object> sendMailToUser(@PathVariable String email)
    {
        if( serviceUtilisateur.sendMailToUser(email) != null)
        {
            return ResponseEntity.ok().body(
                    new Object(){
                        public final String message ="Vérifier votre courrier éléctronique!";
                    }
            );
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé ou service indisponible!");
    }

}
