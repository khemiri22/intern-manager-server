package com.khemiri.InternManager.controllers;

import com.khemiri.InternManager.dto.requests.UserRequest;
import com.khemiri.InternManager.dto.responses.AdminResponse;
import com.khemiri.InternManager.dto.responses.UserResponse;
import com.khemiri.InternManager.dto.responses.TokenPayloadResponse;
import com.khemiri.InternManager.entities.Admin;
import com.khemiri.InternManager.services.admin.IServiceAdmin;
import com.khemiri.InternManager.utils.CookiesManager;
import com.khemiri.InternManager.utils.JwtManager;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {
    private final IServiceAdmin adminService;
    private final JwtManager jwtManager;

    @PostMapping("/register")
    public ResponseEntity<Object> registerAdmin(@RequestBody UserRequest request) {
        Admin admin = adminService.findByEmail(request.getEmail()).orElse(null);
        if (admin == null){
            admin = new Admin();
            admin.setNom(request.getNom());
            admin.setPrenom(request.getPrenom());
            admin.setEmail(request.getEmail());
            admin.setMotDePasse(request.getMotDePasse());
            admin=adminService.save(admin);
            return ResponseEntity.ok().body(new UserResponse(
                    admin.getId(),admin.getNom(),admin.getPrenom(),admin.getEmail(),admin.getRole()
            ));
        }
       return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email existe déjà!");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getAdminById(@PathVariable Long id,@CookieValue(name = "token", required = false)String cookieValue) {
        String token = CookiesManager.getCookie(cookieValue);
        if (token == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access Denied !");
        TokenPayloadResponse tokenPayloadResponse = jwtManager.validateJwtToken(token);
        if(tokenPayloadResponse == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access Denied !");
        if(!tokenPayloadResponse.getRole().equals("admin"))
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access Denied !");
        Admin admin = adminService.findById(id).orElse(null);
        return admin != null ? ResponseEntity.ok().body(new AdminResponse(
                admin.getId()
                ,admin.getNom()
                ,admin.getPrenom()
                ,admin.getEmail()
                ,admin.getRole()
                ,admin.getStagiaires().stream().map(
                        stagiaire -> new UserResponse(
                                stagiaire.getId(),
                                stagiaire.getNom(),
                                stagiaire.getPrenom(),
                                stagiaire.getEmail(),
                                stagiaire.getRole()
                        )
        ).toList())
        ) : ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with ID: " + id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllAdmins(@CookieValue(name = "token", required = false)String cookieValue) {
        String token = CookiesManager.getCookie(cookieValue);
        if (token == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access Denied !");
        TokenPayloadResponse tokenPayloadResponse = jwtManager.validateJwtToken(token);
        if(tokenPayloadResponse == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access Denied !");
        if(!tokenPayloadResponse.getRole().equals("admin"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access Denied !");
        List<UserResponse> admins = adminService.findAll().stream().map(admin ->
             new UserResponse(
                     admin.getId(),admin.getNom(),admin.getPrenom(),admin.getEmail(),admin.getRole()
            )).toList();
        return ResponseEntity.ok().body(admins);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Object> updateAdminById(@PathVariable Long id, @RequestBody UserRequest request, @CookieValue(name = "token", required = false)String cookieValue) {

        String token = CookiesManager.getCookie(cookieValue);
        if (token == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access Denied !");
        TokenPayloadResponse tokenPayloadResponse = jwtManager.validateJwtToken(token);
        if(tokenPayloadResponse == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access Denied !");
        if(!tokenPayloadResponse.getRole().equals("admin"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access Denied !");
        Admin adm = adminService.findById(id).orElse(null);
        if (adm != null){
            adm.setNom(request.getNom());
            adm.setPrenom(request.getPrenom());
            adm.setEmail(request.getEmail());
            adm.setMotDePasse(request.getMotDePasse());
            return ResponseEntity.ok().body(
                    new UserResponse(
                            adm.getId(),adm.getNom(),adm.getPrenom(),adm.getEmail(),adm.getRole()
                    )
            );
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with ID: " + id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteAdmin(@PathVariable Long id,@CookieValue(name = "token", required = false)String cookieValue) {
        String token = CookiesManager.getCookie(cookieValue);
        if (token == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access Denied !");
        TokenPayloadResponse tokenPayloadResponse = jwtManager.validateJwtToken(token);
        if(tokenPayloadResponse == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access Denied !");
        if(!tokenPayloadResponse.getRole().equals("admin"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access Denied !");
        Admin adm = adminService.findById(id).orElse(null);
        if (adm != null)
        {
            adminService.deleteById(id);
            return ResponseEntity.ok().body(
                    new UserResponse(
                            adm.getId(),adm.getNom(),adm.getPrenom(),adm.getEmail(),adm.getRole()
                    )
            );
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with ID: " + id);
    }
}
