package com.example.socialNetwork.controller;

import com.example.socialNetwork.payload.request.LoginRequest;
import com.example.socialNetwork.payload.request.SignupRequest;
import com.example.socialNetwork.payload.response.JWTSuccessResponse;
import com.example.socialNetwork.payload.response.MessageResponse;
import com.example.socialNetwork.security.jwt.JWTProvider;
import com.example.socialNetwork.service.UserService;
import com.example.socialNetwork.validators.ResponseErrorValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    private AuthenticationManager authenticationManager;

    @Autowired
    private ResponseErrorValidator responseErrorValidator;

    @Autowired
    private JWTProvider jwtProvider;

    @Autowired
    private UserService userService;

    // api/auth/signup
    // метод, который будет принимать данные пользователей, чтобы они могли зарегистрироваться
    @PostMapping("/api/auth/signup")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody SignupRequest signupRequest,
                                               BindingResult bindingResult) {
        ResponseEntity<Object> listErrors = responseErrorValidator.mappedValidatorService(bindingResult);
        if (!ObjectUtils.isEmpty(listErrors)) return listErrors;

        // пробуем создать пользователя
        try {
            userService.createUser(signupRequest);
            return ResponseEntity.ok(new MessageResponse("Registration successfully completed"));
        } catch (Exception e) {
            return ResponseEntity.ok(e.getMessage());
        }
    }

    // api/auth/signin
    // метод, который будет давать данные пользователей, чтобы они могли авторизоваться
    @PostMapping("/api/auth/signin")
    public ResponseEntity<Object> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
                                                   BindingResult bindingResult) {
        ResponseEntity<Object> listErrors = responseErrorValidator.mappedValidatorService(bindingResult);
        if (!ObjectUtils.isEmpty(listErrors)) return listErrors;

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUserName(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtProvider.generateToken(authentication);

            JWTSuccessResponse response = new JWTSuccessResponse(true, jwt);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Invalid username or password"));
        }
    }

}
