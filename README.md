# 🎯 Sistema de Gerenciamento de Incidentes

Um sistema completo de gerenciamento de incidentes desenvolvido com **Spring Boot** (backend) e **Angular** (frontend), containerizado com Docker e PostgreSQL.

## 📋 O que faz a aplicação?

Este sistema permite gerenciar incidentes/ocorrências de forma completa e profissional:

### 🔐 **Autenticação e Autorização**
- Sistema de login com JWT (JSON Web Token)
- Controle de acesso baseado em roles (ADMIN/BASIC)
- Proteção de rotas e endpoints

### 📊 **Gerenciamento de Incidentes**
- **Criar** novos incidentes com título, descrição, prioridade e responsável
- **Visualizar** lista paginada com filtros por status, prioridade e busca textual
- **Editar** incidentes existentes
- **Atualizar status** (OPEN, IN_PROGRESS, RESOLVED, CANCELLED)
- **Sistema de tags** para categorização
- **Comentários** para histórico de interações

### 📈 **Dashboard e Estatísticas**
- Visão geral com estatísticas em tempo real
- Distribuição por status e prioridade
- Cards clicáveis para navegação rápida

### 🛠 **Funcionalidades Técnicas**
- API REST completa com documentação Swagger
- Banco de dados PostgreSQL com migrations Flyway
- Validações de entrada robustas
- Tratamento de erros centralizado
- Interface responsiva com Material Design

## 🏗 Arquitetura da Aplicação

```
📦 challenge/
├── 🔧 Backend/          # API Spring Boot + PostgreSQL
│   ├── src/main/java/   # Código Java (Controllers, Services, Entities)
│   ├── src/main/resources/ # Configurações e migrations SQL
│   ├── docker-compose.yml # Orquestração de containers
│   └── Dockerfile       # Container do backend
└── 🎨 Frontend/         # Interface Angular + Material
    ├── src/app/         # Componentes, Services, Models
    ├── src/environments/ # Configurações de ambiente
    └── Dockerfile       # Container do frontend
```

## ⚡ Como executar TUDO

### 📋 Pré-requisitos
- **Docker** e **Docker Compose** instalados
- **Node.js 18+** e **npm** (para desenvolvimento frontend)
- **Java 17+** e **Maven** (para desenvolvimento backend)

---

## 🚀 Opção 1: Executar com Docker (RECOMENDADO)

### 1️⃣ **Executar Backend + Banco de Dados**

```bash
# Navegar para o diretório do backend
cd /home/diego/Desafio-01/challenge/Backend

# Construir o JAR da aplicação
./mvnw clean package -DskipTests

# Subir backend + PostgreSQL
docker-compose up -d

# Verificar se está rodando
docker-compose ps
```

**✅ Backend estará disponível em:** `http://localhost:8080`  
**📚 Documentação Swagger:** `http://localhost:8080/swagger-ui.html`  
**🗄 PostgreSQL:** `localhost:5433` (usuário: `postgres`, senha: `postgres`)

### 2️⃣ **Executar Frontend**

```bash
# Navegar para o diretório do frontend
cd /home/diego/Desafio-01/challenge/Frontend

# Construir a imagem Docker
docker build -t incident-frontend .

# Executar o container
docker run -d -p 4200:4200 --name frontend incident-frontend

# Verificar se está rodando
docker ps | grep frontend
```

**✅ Frontend estará disponível em:** `http://localhost:4200`

---

## 🔑 Credenciais de Teste

O sistema vem com usuários pré-configurados:

| 👤 **Username** | 🔑 **Password** | 🎭 **Role** | 📋 **Descrição** |
|-----------------|-----------------|-------------|-------------------|
| `admin`         | `123`           | ADMIN       | Administrador do sistema |
| `analista`      | `123`           | BASIC       | Analista de incidentes |
| `tecnico`       | `123`           | BASIC       | Técnico de suporte |
| `suporte`       | `123`           | BASIC       | Atendimento ao cliente |

---

## 📋 Comandos Úteis

### 🔧 **Backend**
```bash
# Parar todos os containers
docker-compose down

# Ver logs do backend
docker-compose logs -f java_app

# Acessar banco de dados
docker exec -it java_db psql -U postgres -d java_db

# Rebuild completo
docker-compose down && docker-compose up --build -d
```

### 🎨 **Frontend**
```bash
# Build para produção
npm run build

# Executar testes
npm test

# Parar container do frontend
docker stop frontend && docker rm frontend
```

### 🗄 **Banco de Dados**
```bash
# Backup do banco
docker exec java_db pg_dump -U postgres java_db > backup.sql

# Restaurar backup
docker exec -i java_db psql -U postgres java_db < backup.sql

# Ver tabelas
docker exec -it java_db psql -U postgres -d java_db -c "\dt"
```

---

## 🌟 Como usar a aplicação

1. **Acesse** `http://localhost:4200`
2. **Faça login** com uma das credenciais acima
3. **Explore o dashboard** com estatísticas dos incidentes
4. **Crie um novo incidente** clicando no botão "+"
5. **Gerencie incidentes** através da lista com filtros
6. **Adicione comentários** nos detalhes dos incidentes

---

## 🔗 URLs Importantes

- **Frontend:** http://localhost:4200
- **Backend API:** http://localhost:8080/api
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **PostgreSQL:** localhost:5433

---

## 🛡 Tecnologias Utilizadas

### Backend
- **Spring Boot 3.5.5** - Framework principal
- **Spring Security** - Autenticação e autorização
- **Spring Data JPA** - Persistência de dados
- **PostgreSQL 15** - Banco de dados
- **Flyway** - Migrations de banco
- **Swagger/OpenAPI** - Documentação da API
- **Docker** - Containerização

### Frontend
- **Angular 17** - Framework frontend
- **Angular Material** - Componentes UI
- **TypeScript** - Linguagem de programação
- **RxJS** - Programação reativa
- **SCSS** - Estilização

---

## 🚨 Solução de Problemas

### ❌ **Erro de porta em uso**
```bash
sudo lsof -i :8080
sudo lsof -i :4200

sudo kill -9 <PID>
```

### ❌ **Erro de conexão com banco**
```bash
docker-compose ps

docker-compose restart java_db
```

### ❌ **Frontend não conecta com backend**
- Verificar se backend está rodando em `http://localhost:8080`
- Verificar configuração em `Frontend/src/environments/environment.ts`

---

**🎯 Sistema pronto para uso! Qualquer dúvida, consulte a documentação Swagger ou os logs dos containers.** 