package com.example.challenge.config;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Configuração para geração automática de chaves RSA para JWT/OAuth2
 * As chaves são geradas dinamicamente na inicialização da aplicação
 */
@Configuration
public class KeyConfig {

    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;

    @PostConstruct
    public void generateKeys() {
        try {
            System.out.println("Gerando chaves RSA para OAuth2/JWT...");
            
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            
            this.publicKey = (RSAPublicKey) keyPair.getPublic();
            this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
            
            System.out.println("Chaves OAuth geradas com sucesso!");
            System.out.println("   - Algoritmo: RSA 2048 bits");
            System.out.println("   - Chave pública: " + publicKey.getAlgorithm());
            System.out.println("   - Chave privada: " + privateKey.getAlgorithm());
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar chaves RSA: " + e.getMessage(), e);
        }
    }

    @Bean
    public RSAPublicKey rsaPublicKey() {
        return this.publicKey;
    }

    @Bean
    public RSAPrivateKey rsaPrivateKey() {
        return this.privateKey;
    }
} 