package com.overcode250204.smartlogicticssystem.configs;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;


import java.io.IOException;
import java.io.InputStream;


@Configuration
public class FirebaseConfig {


        @Value("${firebase.config-path:firebase-service-account.json}")
        private String configPath;

        @Bean
        public FirebaseApp initializeFirebase() throws IOException {
            if (!FirebaseApp.getApps().isEmpty()) {
                return FirebaseApp.getInstance();
            }

            ClassPathResource resource = new ClassPathResource(configPath);

            if (!resource.exists()) {
                throw new IOException("Missing firebase-service-account.json file");
            }

            try (InputStream serviceAccount = resource.getInputStream()) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp app = FirebaseApp.initializeApp(options);
                return app;
            }
        }

}
