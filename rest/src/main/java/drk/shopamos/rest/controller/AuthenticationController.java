package drk.shopamos.rest.controller;

import drk.shopamos.rest.controller.request.AuthenticationRequest;
import drk.shopamos.rest.controller.response.AuthenticationResponse;
import drk.shopamos.rest.service.AuthenticationService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private final AuthenticationService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @Valid @RequestBody AuthenticationRequest request) {
        String jwtToken = authService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(AuthenticationResponse.builder().jwtToken(jwtToken).build());
    }
}
