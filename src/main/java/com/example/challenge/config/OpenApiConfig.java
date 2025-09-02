package com.example.challenge.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(buildApiInformation())           
            .servers(buildServerList())            
            .components(buildSecurityComponents()) 
            .addSecurityItem(buildGlobalSecurity()); 
    }
    
    private Info buildApiInformation() {
        return new Info()
            .title("🎯 Incidents Management API")
            .description(buildRichDescription())
            .version("1.0.0")
            .contact(buildContactInformation())
            .license(buildLicenseInformation());
    }

    private String buildRichDescription() {
        return """
            # 🚀 API Completa de Gestão de Ocorrências (Incidents)
            
            Esta API RESTful fornece um sistema completo para gerenciamento de incidents,
            comentários e usuários, com autenticação JWT e recursos avançados de filtragem.
            
            ---
            
            ## 🔐 **Como Autenticar na API**
            
            A API utiliza **JWT Bearer Token** para autenticação. Siga estes passos:
            
            ### 📝 **Passo a Passo:**
            1. **Faça Login**: Execute `POST /api/login` com suas credenciais
            2. **Copie o Token**: Do campo `accessToken` na resposta
            3. **Autorize**: Clique no botão **"Authorize"** 🔒 no topo desta página
            4. **Cole o Token**: No formato `Bearer seu-jwt-token-aqui`
            5. **Confirme**: Clique em "Authorize" e pronto! 🎉
            
            ### ⏰ **Importante:**
            - O token expira em **5 minutos**
            - Renove quando necessário fazendo novo login
            - Use sempre o prefixo `Bearer ` antes do token
            
            ---
            
            ## 👥 **Usuários de Demonstração**
            
            O sistema vem com usuários pré-configurados para testes:
            
            | 👤 **Username** | 🔑 **Password** | 🎭 **Role** | 📋 **Descrição** |
            |-----------------|-----------------|-------------|-------------------|
            | `admin`         | `123`           | ADMIN       | Administrador do sistema |
            | `analista`      | `123`           | BASIC       | Analista de incidents |
            | `tecnico`       | `123`           | BASIC       | Técnico de suporte |
            | `suporte`       | `123`           | BASIC       | Atendimento ao cliente |
            
            ---
            
            ## **Recursos Principais**
            
            ### **Gestão de Incidents**
            -  **CRUD Completo**: Criar, listar, atualizar e deletar incidents
            -  **Filtros Avançados**: Por status, prioridade e busca textual
            -  **Paginação Inteligente**: Controle total sobre resultados
            -  **Ordenação Flexível**: Por qualquer campo, ASC ou DESC
            -  **Busca Combinada**: Título e descrição simultaneamente
            
            ### **Sistema de Comentários**
            -  **Comentários por Incident**: Histórico completo de interações
            -  **CRUD de Comentários**: Gerenciamento total dos comentários
            -  **Contadores**: Quantidade de comentários por incident
            -  **Timestamps Automáticos**: Controle de criação e atualizaçã
            
            ### **Gerenciamento de Usuários**
            -  **Registro Público**: Criação de conta sem autenticação
            -  **Gestão de Roles**: Sistema de permissões (ADMIN/BASIC)
            -  **Busca por Username**: Localização rápida de usuários
            -  **Segurança**: Senhas criptografadas com BCrypt
            
            ### **Estatísticas e Relatórios**
            -  **Dashboard de Stats**: Visão geral do sistema
            -  **Métricas por Status**: Quantidades por cada status
            -  **Métricas por Prioridade**: Distribuição de prioridades
            -  **Totalizadores**: Contadores gerais do sistema
            
            ---
            
            ## **Tecnologias Utilizadas**
            
            - **Spring Boot 3.5.5** - Framework principal
            - **Spring Security** - Autenticação e autorização
            - **JWT** - Tokens de acesso seguros
            - **PostgreSQL** - Banco de dados relacional
            - **Spring Data JPA** - Persistência de dados
            - **Flyway** - Migração de banco de dados
            - **SpringDoc OpenAPI 2.7.0** - Documentação automática
            
            ---
            
            ## **Exemplo Prático de Uso**
            
            ```bash
            # 1. Fazer login e obter token
            curl -X POST http://localhost:8080/api/login \\
              -H "Content-Type: application/json" \\
              -d '{"username":"admin","password":"123"}'
            
            # 2. Usar o token retornado para acessar endpoints protegidos
            curl -X GET http://localhost:8080/api/incidents \\
              -H "Authorization: Bearer SEU_TOKEN_AQUI"
            
            # 3. Criar um novo incident
            curl -X POST http://localhost:8080/api/incidents \\
              -H "Authorization: Bearer SEU_TOKEN_AQUI" \\
              -H "Content-Type: application/json" \\
              -d '{
                "title": "Servidor não responde",
                "description": "Problema de conectividade",
                "priority": "HIGH",
                "status": "OPEN",
                "responsibleEmail": "admin@empresa.com",
                "tags": ["servidor", "urgente"]
              }'
            ```
            """;
    }
    

    private Contact buildContactInformation() {
        return new Contact()
            .name("Via Appia Informática - Equipe de Desenvolvimento")
            .email("contato@viaappia.com.br")
            .url("https://viaappia.com.br");
    }
    private License buildLicenseInformation() {
        return new License()
            .name("MIT License")
            .url("https://opensource.org/licenses/MIT");
    }
    
  
    private List<Server> buildServerList() {
        Server localServer = new Server()
            .url("http://localhost:8080")
            .description("🏠 Servidor Local de Desenvolvimento");
        
        
        return List.of(localServer);
    }
    
    private Components buildSecurityComponents() {
        return new Components()
            .addSecuritySchemes("bearerAuth", buildJwtSecurityScheme());
    }

    private SecurityScheme buildJwtSecurityScheme() {
        return new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .description("""
                🔐 **Autenticação JWT Bearer Token**
                
                Para autenticar suas requisições nesta API:
                
                1️⃣ **Obtenha um token**: Faça login via `POST /api/login`
                2️⃣ **Copie o token**: Do campo `accessToken` na resposta
                3️⃣ **Use o formato**: `Bearer {seu-token-jwt-aqui}`
                
                **📋 Exemplo de token:**
                ```
                Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJzZWxmIiwic3ViIjoiYWRtaW4iLCJleHAiOjE3MDk1ODM2MzAsImlhdCI6MTcwOTU4MzMzMCwic2NvcGUiOiJBRE1JTiJ9...
                ```
                
                ⚠️ **Importante**: O token expira em 5 minutos. Renove conforme necessário.
                
                ✅ **Dica**: Após colar o token aqui, ele será automaticamente incluído em todas as requisições!
                """);
    }
    
    private SecurityRequirement buildGlobalSecurity() {
        return new SecurityRequirement().addList("bearerAuth");
    }
} 