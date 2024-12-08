package com.khemiri.InternManager.controllers;

import com.khemiri.InternManager.dto.requests.StagiaireRequest;
import com.khemiri.InternManager.dto.responses.*;
import com.khemiri.InternManager.dto.requests.UserRequest;
import com.khemiri.InternManager.entities.Admin;
import com.khemiri.InternManager.entities.Stage;
import com.khemiri.InternManager.entities.Stagiaire;
import com.khemiri.InternManager.entities.Utilisateur;
import com.khemiri.InternManager.services.admin.IServiceAdmin;
import com.khemiri.InternManager.services.stagiaire.IServiceStagiaire;
import com.khemiri.InternManager.utils.CookiesManager;
import com.khemiri.InternManager.utils.JwtManager;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/intern")
@AllArgsConstructor
public class StagiaireController {

    private IServiceStagiaire serviceStagiaire;
    private IServiceAdmin serviceAdmin;
    private JwtManager jwtManager;

    @PostMapping("/register")
    public ResponseEntity<Object> registerIntern(@ModelAttribute StagiaireRequest request, @CookieValue(name = "token", required = false)String cookieValue) {
        String token = CookiesManager.getCookie(cookieValue);
        if (token == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        TokenPayloadResponse tokenPayloadResponse = jwtManager.validateJwtToken(token);
        if(tokenPayloadResponse == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        if(!tokenPayloadResponse.getRole().equals("admin"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        Stagiaire stagiaire = serviceStagiaire.findByEmail(request.getEmail()).orElse(null);
        if(stagiaire != null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email est exist déjà !");
        }
        Admin admin = serviceAdmin.findById(Long.parseLong(tokenPayloadResponse.getId())).orElse(null);
        if(admin == null)
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin non trouvé !");

        try {
            String path = serviceStagiaire.saveImage(request.getImage());
            stagiaire = new Stagiaire();
            stagiaire.setNom(request.getNom());
            stagiaire.setPrenom(request.getPrenom());
            stagiaire.setEmail(request.getEmail());
            stagiaire.setMotDePasse(request.getMotDePasse());
            stagiaire.setAdmin(admin);
            stagiaire.setStages(new ArrayList<>());
            stagiaire.setImageDeStagiaire(path);
            stagiaire = serviceStagiaire.save(stagiaire);
            return ResponseEntity.ok().body(
                    new UserResponse(
                            stagiaire.getId(),stagiaire.getNom(),stagiaire.getPrenom(),stagiaire.getEmail(),stagiaire.getRole()
                    ));
        }catch (IOException ignored)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Image upload a échoué !");
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getInternById(@PathVariable Long id,@CookieValue(name = "token", required = false)String cookieValue) {
        String token = CookiesManager.getCookie(cookieValue);
        if (token == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        TokenPayloadResponse tokenPayloadResponse = jwtManager.validateJwtToken(token);
        if(tokenPayloadResponse == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        Stagiaire stagiaire = serviceStagiaire.findById(id).orElse(null);
        return stagiaire != null ? ResponseEntity.ok().body(new StagiaireResponse(
                stagiaire.getId()
                ,stagiaire.getNom()
                ,stagiaire.getPrenom()
                ,stagiaire.getEmail()
                ,stagiaire.getImageDeStagiaire()
                ))
                :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("Stagiaire non trouvé !");

    }

    @GetMapping("/userInfo")
    public ResponseEntity<Object> getIntern(@CookieValue(name = "token", required = false)String cookieValue) {
        String token = CookiesManager.getCookie(cookieValue);
        if (token == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        TokenPayloadResponse tokenPayloadResponse = jwtManager.validateJwtToken(token);
        if(tokenPayloadResponse == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        Stagiaire stagiaire = serviceStagiaire.findById(Long.parseLong(tokenPayloadResponse.getId())).orElse(null);
        return stagiaire != null ? ResponseEntity.ok().body(new StagiaireResponse(
                stagiaire.getId()
                ,stagiaire.getNom()
                ,stagiaire.getPrenom()
                ,stagiaire.getEmail()
                ,stagiaire.getImageDeStagiaire()
        ))
                :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("Stagiaire non trouvé !");

    }

    @GetMapping
    public ResponseEntity<Object> getAllInterns(@CookieValue(name = "token", required = false)String cookieValue) {
        String token = CookiesManager.getCookie(cookieValue);
        if (token == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        TokenPayloadResponse tokenPayloadResponse = jwtManager.validateJwtToken(token);
        if(tokenPayloadResponse == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        if(!tokenPayloadResponse.getRole().equals("admin"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        Admin admin = serviceAdmin.findById(Long.parseLong(tokenPayloadResponse.getId())).orElse(null);
        if(admin == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin non trouve !");
        List<StagiaireResponse> interns = serviceStagiaire.findAll().stream().filter(stagiaire -> stagiaire.getAdmin()==admin).map(
                stagiaire -> StagiaireResponse.builder()
                        .image(stagiaire.getImageDeStagiaire())
                        .email(stagiaire.getEmail())
                        .id(stagiaire.getId())
                        .nom(stagiaire.getNom())
                        .prenom(stagiaire.getPrenom())
                        .build()
        ).toList();
        return ResponseEntity.ok().body(interns);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Object> updateInternById(@PathVariable Long id,@ModelAttribute StagiaireRequest request,@CookieValue(name = "token", required = false)String cookieValue) {
        System.out.println(request);
        String token = CookiesManager.getCookie(cookieValue);
        if (token == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        TokenPayloadResponse tokenPayloadResponse = jwtManager.validateJwtToken(token);
        if (tokenPayloadResponse == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        if (!tokenPayloadResponse.getRole().equals("admin"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        String path = "";
        if (request.getImage() != null) {
            try {
                path = serviceStagiaire.saveImage(request.getImage());
            } catch (IOException ignored) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Image upload a échoué !");
            }
        }
        Stagiaire stg = new Stagiaire();
        stg.setNom(request.getNom());
        stg.setPrenom(request.getPrenom());
        stg.setEmail(request.getEmail());
        stg.setMotDePasse(request.getMotDePasse());
        stg.setImageDeStagiaire(path);
        Stagiaire result = serviceStagiaire.update(stg, id);
        if(result != null)
            return ResponseEntity.ok().body(
                    new StagiaireResponse(
                            result.getId(),result.getNom(),result.getPrenom(),result.getEmail(),result.getImageDeStagiaire()
                    )
            );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Stagiaire non trouvé !");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteIntern(@PathVariable Long id,@CookieValue(name = "token", required = false)String cookieValue) {
        String token = CookiesManager.getCookie(cookieValue);
        if (token == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        TokenPayloadResponse tokenPayloadResponse = jwtManager.validateJwtToken(token);
        if(tokenPayloadResponse == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        if(!tokenPayloadResponse.getRole().equals("admin"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        Stagiaire stg = serviceStagiaire.findById(id).orElse(null);
        if (stg != null)
        {
            serviceStagiaire.deleteById(id);
            return ResponseEntity.ok().body(new UserResponse(
                    stg.getId(),stg.getNom(),stg.getPrenom(),stg.getEmail(),stg.getRole()
            ));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Stagiaire non trouvé !");
    }

    @GetMapping("/getAllStageOfInternByAdmin/{id_intern}")
    public ResponseEntity<Object> getAllStageOfIntern(@PathVariable Long id_intern,@CookieValue(name = "token", required = false)String cookieValue) {
        String token = CookiesManager.getCookie(cookieValue);
        if (token == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        TokenPayloadResponse tokenPayloadResponse = jwtManager.validateJwtToken(token);
        if (tokenPayloadResponse == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        if(!tokenPayloadResponse.getRole().equals("admin"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        Stagiaire stagiaire = serviceStagiaire.findById(id_intern).orElse(null);
        if (stagiaire == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Stagiaire non trouvé !");
        List<StageResponse> stages = stagiaire.getStages().stream().map(
                stage -> new StageResponse(
                        stage.getId(),new SimpleDateFormat("yyyy-MM-dd").format(stage.getDateDebut()),new SimpleDateFormat("yyyy-MM-dd").format(stage.getDateFin()),stage.getTuteur(),stage.getSujet(),stage.getDescription(),stage.getEtablissement()
                )).toList();
        return ResponseEntity.ok().body(stages);
    }
    @GetMapping("/getAllStageOfInternByAdmin")
    public ResponseEntity<Object> getAllStageOfIntern(@CookieValue(name = "token", required = false)String cookieValue) {
        String token = CookiesManager.getCookie(cookieValue);
        if (token == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        TokenPayloadResponse tokenPayloadResponse = jwtManager.validateJwtToken(token);
        if (tokenPayloadResponse == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        if(!tokenPayloadResponse.getRole().equals("intern"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        Stagiaire stagiaire = serviceStagiaire.findById(Long.parseLong(tokenPayloadResponse.getId())).orElse(null);
        if (stagiaire == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Stagiaire non trouvé !");
        List<StageResponse> stages = stagiaire.getStages().stream().map(
                stage -> new StageResponse(
                        stage.getId(),new SimpleDateFormat("yyyy-MM-dd").format(stage.getDateDebut()),new SimpleDateFormat("yyyy-MM-dd").format(stage.getDateFin()),stage.getTuteur(),stage.getSujet(),stage.getDescription(),stage.getEtablissement()
                )).toList();
        return ResponseEntity.ok().body(stages);
    }
}
