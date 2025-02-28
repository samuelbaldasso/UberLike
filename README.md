# Uber-Like App Backend

Este repositório contém o backend de um aplicativo estilo Uber para motoboys, desenvolvido com Java e Spring Boot, utilizando RBAC, JWT para segurança, WebSockets para comunicação em tempo real, Docker para conteinerização, PostgreSQL para persistência e Maven para gestão de dependências, seguindo a arquitetura limpa.

## Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3**
  - Spring Security (JWT e RBAC)
  - Spring WebSockets
  - Spring Data JPA
- **PostgreSQL**
- **Docker**
- **Maven**
- **Arquitetura Limpa**

## Funcionalidades Principais

- **Autenticação e Autorizção**: JWT com RBAC para controle de acesso baseado em funções (usuário, motoboy, admin).
- **Gerenciamento de Perfis**: Edição e visualização de perfis.
- **Geolocalização e Mapeamento**: Integração para seleção de localização.
- **Solicitação de Corridas**: Criação, aceitação/rejeição de solicitações.
- **Atualização em Tempo Real**: WebSockets para rastreamento de status.

## Como Rodar o Projeto

### 1. Clonar o Repositório
```sh
 git clone https://github.com/samuelbaldasso/Uber_Like-Java-Spring-RBAC-JWT-Security-WebSockets-Docker-PostgreSQL-Maven-Clean_Arch.git
 cd Uber_Like-Java-Spring-RBAC-JWT-Security-WebSockets-Docker-PostgreSQL-Maven-Clean_Arch
```

### 2. Configurar o Banco de Dados
Crie um banco PostgreSQL e ajuste o `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/uber_like_db
    username: postgres
    password: sua_senha
```

### 3. Rodar com Docker
```sh
docker-compose up -d
```

### 4. Rodar o Backend
```sh
mvn clean install
mvn spring-boot:run
```

## Documentação da API
A API está documentada com **Swagger**. Após iniciar o backend, acesse:
```sh
http://localhost:8080/swagger-ui.html
```

## Estrutura do Projeto
```
Uber_Like-Java-Spring/
├── src/main/java/com/uberlike/
│   ├── config/       # Configurações gerais do sistema
│   ├── controller/   # Controladores da API REST
│   ├── service/      # Serviços e regras de negócio
│   ├── repository/   # Repositórios JPA
│   ├── security/     # Configuração de segurança e JWT
│   ├── model/        # Entidades e modelos
│   └── websocket/    # Configuração e serviço de WebSockets
├── src/main/resources/
│   ├── application.yml  # Configurações principais
│   ├── db/migration/    # Scripts de migração do banco
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

## Como Contribuir
1. Faça um fork do projeto.
2. Crie uma branch com a feature (`git checkout -b feature-nova`).
3. Commit suas alterações (`git commit -m 'Adiciona nova feature'`).
4. Faça push para a branch (`git push origin feature-nova`).
5. Abra um Pull Request.

## Licença
Este projeto está licenciado sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

