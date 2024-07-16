package org.example.be.controller;

import org.example.be.model.JwtToken;
import org.example.be.respository.TokenRespository;
import org.example.be.service.impl.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/token")
public class TokenController {
    @Autowired
    private TokenRespository tokenRespository;

    @Autowired
    private JwtService jwtService;

    @GetMapping("/checkToken")
    public ResponseEntity<?> checkToken(@RequestParam(name = "token") String token) {
        try {
            JwtToken jwtToken = tokenRespository.findByTokenEquals(token);
            if (jwtService.validateJwtToken(token) && jwtToken != null){
                return new ResponseEntity<>("Good Token" ,HttpStatus.OK);
            }
        }catch (Exception e){
            return new ResponseEntity<>("Bad Token", HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
