# 🚀 Incident Management System - Frontend

Uma aplicação Angular moderna e responsiva para gerenciamento de ocorrências (incidents) com autenticação JWT, filtros avançados, paginação e interface Material Design.

## 📋 Características

### 🔐 Autenticação e Autorização
- Sistema de login com JWT Bearer Token
- Interceptor HTTP automático para anexar tokens
- Guards de rota para proteção de páginas
- Logout automático em caso de token expirado
- Suporte a diferentes níveis de permissão (ADMIN/BASIC)

### 📊 Dashboard Interativo
- Visão geral com estatísticas em tempo real
- Gráficos de status e prioridade dos incidents
- Cards clicáveis para navegação rápida
- Ações rápidas para criação e visualização

### 🔍 Lista de Incidents Avançada
- Filtros por status, prioridade e busca textual
- Paginação com opções de tamanho de página
- Ordenação por múltiplos campos
- Cards responsivos com informações resumidas
- Ações inline (visualizar, editar, excluir)

### ✏️ Formulários Inteligentes
- Formulários reativos com validação em tempo real
- Sistema de tags dinâmico
- Normalização automática de dados (DRY principle)
- Feedback visual para estados de loading e erro

### 💬 Sistema de Comentários
- Adição de comentários em tempo real
- Histórico completo de interações
- Interface intuitiva e responsiva
- Autor automático baseado no usuário logado

### 🎨 Design Moderno
- Material Design com Angular Material
- Interface responsiva para mobile e desktop
- Temas consistentes e acessibilidade
- Animações suaves e feedback visual

## 🛠 Tecnologias Utilizadas

- **Angular 17** - Framework principal
- **Angular Material 17** - Componentes de UI
- **TypeScript** - Linguagem de programação
- **RxJS** - Programação reativa
- **SCSS** - Estilização avançada
- **JWT** - Autenticação
- **Docker** - Containerização

## 📁 Estrutura do Projeto

```
src/
├── app/
│   ├── components/           # Componentes da aplicação
│   │   ├── login/           # Tela de login
│   │   ├── register/        # Tela de registro
│   │   ├── dashboard/       # Dashboard principal
│   │   ├── incident-list/   # Lista de incidents
│   │   ├── incident-detail/ # Detalhes do incident
│   │   └── incident-form/   # Formulário de incident
│   ├── services/            # Serviços da aplicação
│   │   ├── auth.service.ts     # Autenticação
│   │   ├── api-client.service.ts # Cliente API genérico
│   │   ├── incident.service.ts  # Gerenciamento de incidents
│   │   ├── comment.service.ts   # Gerenciamento de comentários
│   │   └── user.service.ts      # Gerenciamento de usuários
│   ├── models/              # Modelos TypeScript
│   ├── guards/              # Guards de rota
│   ├── interceptors/        # Interceptors HTTP
│   └── utils/               # Utilitários (pipes, helpers)
├── environments/            # Configurações de ambiente
└── assets/                  # Recursos estáticos
```

## 🚀 Como Executar

### Pré-requisitos
- Node.js 18+ 
- npm 9+
- Backend da aplicação rodando na porta 8080

### 1. Instalação das Dependências

```bash
cd /home/diego/Desafio-01/front/incident-management
npm install
```

### 2. Configuração do Ambiente

Verifique se o arquivo `src/environments/environment.ts` está configurado corretamente:

```typescript
export const environment = {
  production: false,
  apiBaseUrl: 'http://localhost:8080/api'
};
```

### 3. Executar em Modo de Desenvolvimento

```bash
npm start
# ou
ng serve
```

A aplicação estará disponível em: **http://localhost:4200**

### 4. Build para Produção

```bash
npm run build
# ou
ng build --configuration production
```

## 🐳 Executar com Docker

### 1. Build da Imagem

```bash
cd /home/diego/Desafio-01/front/incident-management
docker build -t incident-management-frontend .
```

### 2. Executar Container

```bash
docker run -p 4200:80 incident-management-frontend
```

A aplicação estará disponível em: **http://localhost:4200**

## 🔑 Credenciais de Demonstração

O sistema vem com usuários pré-configurados para testes:

| 👤 **Username** | 🔑 **Password** | 🎭 **Role** | 📋 **Descrição** |
|-----------------|-----------------|-------------|-------------------|
| `admin`         | `123`           | ADMIN       | Administrador do sistema |
| `analista`      | `123`           | BASIC       | Analista de incidents |
| `tecnico`       | `123`           | BASIC       | Técnico de suporte |
| `suporte`       | `123`           | BASIC       | Atendimento ao cliente |

## 🌟 Funcionalidades Principais

### 1. **Login e Autenticação**
- Acesse `/login` para fazer login
- Use as credenciais de demonstração acima
- O token JWT é armazenado automaticamente
- Redirecionamento automático para o dashboard

### 2. **Dashboard**
- Visão geral com estatísticas dos incidents
- Cards clicáveis para filtrar por status
- Distribuição por prioridade
- Ações rápidas para criar novos incidents

### 3. **Gerenciamento de Incidents**
- **Listar**: `/incidents` - Lista com filtros e paginação
- **Criar**: `/incidents/new` - Formulário de criação
- **Visualizar**: `/incidents/:id` - Detalhes completos
- **Editar**: `/incidents/:id/edit` - Formulário de edição

### 4. **Sistema de Comentários**
- Adicione comentários em qualquer incident
- Histórico cronológico de interações
- Autor automático baseado no usuário logado

### 5. **Filtros e Busca**
- Busca textual em título e descrição
- Filtros por status e prioridade
- Ordenação por múltiplos campos
- Paginação configurável

## 🔧 Comandos Úteis

```bash
# Desenvolvimento
npm start                    # Inicia servidor de desenvolvimento
npm run build               # Build para produção
npm run test                # Executa testes unitários
npm run lint                # Verifica qualidade do código

# Docker
docker build -t incident-frontend .     # Build da imagem
docker run -p 4200:80 incident-frontend # Executar container
```

## 🏗 Arquitetura e Padrões

### **Princípio DRY (Don't Repeat Yourself)**
A aplicação implementa várias estratégias para evitar duplicação de código:

1. **ApiClientService**: Cliente HTTP genérico para todas as operações CRUD
2. **Normalização de Dados**: Funções centralizadas para tratar formulários
3. **DateFormatterPipe**: Formatação consistente de datas em toda aplicação
4. **Tratamento de Erros**: Interceptor centralizado para manejo de erros
5. **Query Params Builder**: Construção padronizada de parâmetros de busca

### **Segurança**
- Autenticação JWT obrigatória
- Interceptor automático para anexar tokens
- Logout automático em tokens expirados
- Guards de rota para proteção de páginas
- Validação de formulários no frontend e backend

### **Performance**
- Lazy loading de componentes
- Paginação eficiente
- Debounce em campos de busca
- Cache de dados quando apropriado
- Otimização de bundles

## 🔗 Integração com Backend

A aplicação se conecta com a API REST do backend através dos seguintes endpoints:

- `POST /api/login` - Autenticação
- `GET /api/incidents` - Lista paginada de incidents
- `POST /api/incidents` - Criar novo incident
- `GET /api/incidents/{id}` - Buscar incident por ID
- `PUT /api/incidents/{id}` - Atualizar incident
- `PATCH /api/incidents/{id}/status` - Atualizar apenas status
- `DELETE /api/incidents/{id}` - Excluir incident
- `GET /api/incidents/{id}/comments` - Comentários do incident
- `POST /api/incidents/{id}/comments` - Adicionar comentário
- `GET /api/stats/incidents` - Estatísticas para dashboard

## 📱 Responsividade

A aplicação é totalmente responsiva e funciona perfeitamente em:
- 📱 **Mobile** (320px+)
- 📱 **Tablet** (768px+)
- 💻 **Desktop** (1024px+)
- 🖥 **Large Desktop** (1440px+)

## 🎯 Próximos Passos

- [ ] Implementar notificações push
- [ ] Adicionar mais filtros avançados
- [ ] Sistema de anexos para incidents
- [ ] Relatórios e exportação de dados
- [ ] Modo escuro/claro
- [ ] Internacionalização (i18n)

## 🤝 Contribuição

Este projeto foi desenvolvido como parte de um desafio técnico. Para contribuições:

1. Fork o projeto
2. Crie uma branch para sua feature
3. Commit suas mudanças
4. Push para a branch
5. Abra um Pull Request

## 📄 Licença

Este projeto é parte de um processo seletivo e está disponível para fins educacionais.

---

**Desenvolvido com ❤️ usando Angular e Material Design**
