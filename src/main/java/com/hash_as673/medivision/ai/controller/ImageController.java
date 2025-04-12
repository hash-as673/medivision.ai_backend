package com.hash_as673.medivision.ai.controller;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.hash_as673.medivision.ai.dtos.PredictionResponseDTO;

@RestController
@RequestMapping("/api")
public class ImageController {

    @PostMapping("/upload")
    public ResponseEntity<PredictionResponseDTO> handleImageUpload(@RequestParam("file") MultipartFile file) {
        try {
            // Validate file type
            if (file.isEmpty() || !isValidImageFile(file)) {
                return ResponseEntity.badRequest().body(new PredictionResponseDTO(null, "Invalid file type. Please upload a valid image."));
            }

            
            String prediction = processImage(file);

            PredictionResponseDTO responseDTO = new PredictionResponseDTO(prediction, null);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new PredictionResponseDTO(null, e.getMessage()));
        }
    }

    private boolean isValidImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType.equals("image/jpeg") || contentType.equals("image/png");
    }

    

private String processImage(MultipartFile file) {
    try {
        byte[] imageBytes = file.getBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(imageBytes) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8000/predict/", requestEntity, String.class);

    
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(response.getBody());
        return rootNode.get("prediction").asText(); 
    } catch (Exception e) {
        throw new RuntimeException("Failed to process image: " + e.getMessage());
    }
}
}