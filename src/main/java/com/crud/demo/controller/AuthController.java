package com.crud.demo.controller;

import com.crud.demo.payload.request.LoginRequest;
import com.crud.demo.payload.request.MessageResponse;
import com.crud.demo.payload.request.SignUpRequest;
import com.crud.demo.payload.response.JWTTokenSuccessResponse;
import com.crud.demo.security.JWTTokenProvider;
import com.crud.demo.security.SecurityConstants;
import com.crud.demo.utility.ResponseErrorBuilder;
import com.crud.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api/auth")
@PreAuthorize("permitAll()")
public class AuthController {

    @Autowired
    private JWTTokenProvider jwtTokenProvider;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private ResponseErrorBuilder responseErrorBuilder;
    @Autowired
    private UserService userService;

    @PostMapping("/signin")
    public ResponseEntity<Object> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult) {
        ResponseEntity<Object> errorReponse = responseErrorBuilder.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errorReponse)) return errorReponse;

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        ));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = SecurityConstants.TOKEN_PREFIX + jwtTokenProvider.generateToken(authentication);

        return ResponseEntity.ok(new JWTTokenSuccessResponse(true, jwt));
    }


    @PostMapping("/signup")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody SignUpRequest signupRequest, BindingResult bindingResult) {

        ResponseEntity<Object> errorResponse = responseErrorBuilder.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errorResponse)) return errorResponse;

        userService.createUser(signupRequest);
        return ResponseEntity.ok(new MessageResponse("Please see your inbox to verify your account!"));
    }

    @GetMapping("/verification")
    public void verifyUser(@RequestParam String code) {
        userService.verifyUser(code);
    }



}