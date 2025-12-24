package com.k8x.test1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private static final String STORAGE_PATH = "/app/data/test-file.txt";
    
    @GetMapping("/hello")
    public String hello() {
        return "Hello, Spring Boot!";
    }

  
    @GetMapping("/store/check")
    public String checkStorage() {
        try {
            Path path = Path.of(STORAGE_PATH);
            // 폴더가 없으면 생성
            Files.createDirectories(path.getParent());

            // 파일이 없으면 생성
            if (!Files.exists(path)) {
                Files.writeString(path, "Hello Kubernetes PVC!");
            }

            // 파일 읽어서 반환
            String content = Files.readString(path);
            return "PVC mount path is working. File content: " + content;

        } catch (IOException e) {
            e.printStackTrace();
            return "❌ Failed to access PVC mount path: " + e.getMessage();
        }
    }
}


