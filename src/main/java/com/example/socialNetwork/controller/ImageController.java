package com.example.socialNetwork.controller;

import com.example.socialNetwork.entity.Image;
import com.example.socialNetwork.payload.response.MessageResponse;
import com.example.socialNetwork.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/image")
@CrossOrigin
public class ImageController {

    @Autowired
    ImageService imageService;

    // метод загружает фотографию в профиль пользователя
    @PostMapping("/upload")
    public ResponseEntity<MessageResponse> uploadImageToProfile(@RequestParam("file") MultipartFile file,
                                                                Principal principal) throws IOException {
        imageService.uploadImageToProfile(file, principal);
        return ResponseEntity.ok(new MessageResponse("Image uploaded successfully"));
    }

    // метод получения фотографии профиля пользователя (getUserProfileImage) ("/profileImage")
    @GetMapping("/profileImage")
    public ResponseEntity<Image> getUserProfileImage(Principal principal) {
        Image profileImage = imageService.getUserProfileImage(principal);
        return new ResponseEntity<>(profileImage, HttpStatus.OK);
    }
}
