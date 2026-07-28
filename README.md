#  Maktaba API 

> Plataforma de assinatura para recebimento de livros mensais.

##  Tecnologias

- Java 25 + Spring Boot 4
- PostgreSQL 15 via Docker
- Spring Security + BCrypt
- Thymeleaf + Bootstrap 5
- Lombok + Spring Data JPA
- JavaMailSender (Gmail)

##  Como rodar

### Pré-requisitos
- Java 25
- Docker Desktop
- IntelliJ IDEA

### Passo a passo

1. Clone o repositório:
```bash
git clone https://github.com/thuralvesss/maktada-api.git
```

2. Suba o banco de dados:
```bash
cd Projeto-Maktaba
docker-compose up -d
```

3. Configure o `application.properties` com suas credenciais de email.

4. Rode a aplicação pelo IntelliJ (MaktabaApiApplication).

5. Acesse: http://localhost:8080

##  Rotas principais

| Rota | Descrição | Acesso |
|------|-----------|--------|
| / | Home | Logado |
| /sobre | Página pública com planos | Público |
| /usuarios/cadastro | Cadastro | Público |
| /usuarios/login | Login | Público |
| /livros/catalogo | Catálogo de livros | Público |
| /assinatura/planos | Planos de assinatura | Logado |
| /assinatura/contrato | Contrato de assinatura | Logado |
| /assinatura/status | Status da assinatura | Logado |
| /perfil | Perfil do usuário | Logado |
| /perfil/generos | Escolha de gêneros | Logado |
| /leitura | Acompanhamento de leitura | Logado |
| /recuperar-senha | Recuperação de senha | Público |
| /livros/admin/novo | Cadastro de livros | Admin |
| /admin/dashboard | Dashboard administrativo | Admin |

##  Usuários padrão

- **Admin:** criado diretamente no banco com ROLE_ADMIN
- **Usuário:** cadastrado pelo formulário, recebe ROLE_USER automaticamente

##  Banco de dados

Porta: **5433** (Docker) — evita conflito com PostgreSQL local

```
Host: localhost
Port: 5433
Database: maktada_db
User: user
Password: root
```

##  Funcionalidades

-  Cadastro e login com autenticação
-  Recuperação de senha por email
-  Catálogo de livros com capas
-  Upload de PDF dos livros
-  Acompanhamento de leitura com progresso
-  Planos de assinatura (Básico, Standard, Premium)
-  Contrato de assinatura com aceite de termos
-  Escolha de gêneros literários (1 a 8)
-  Perfil do usuário
-  Dashboard administrativo com relatórios
-  Controle de acesso (Admin vs Usuário)
