# User CRUD - Java Spring H2 Basic Security JPA

## Descrição
Este é um projeto de CRUD de usuários utilizando Java, Spring Boot, H2 Database, JPA e Spring Security para autenticação básica.

## Tecnologias Utilizadas
- Java 17+
- Spring Boot 3+
- Spring Data JPA
- Spring Security (Autenticação básica)
- H2 Database (Banco de dados em memória)
- Maven

## Configuração do Projeto

### Clonar o Repositório
```sh
git clone https://github.com/samuelbaldasso/User_CRUD-Java-Spring-H2-Basic_Security-JPA.git
cd User_CRUD-Java-Spring-H2-Basic_Security-JPA
```

### Configurar o Banco de Dados
O projeto está configurado para usar o banco H2 em memória. Para acessar o console do H2, utilize:

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:tcombo`
- Usuário: `samuel`
- Senha: `admin1234`

### Executar o Projeto
```sh
mvn spring-boot:run
```
O projeto estará disponível em `http://localhost:8080`

## Endpoints da API

### Endpoints CRUD do Usuário

| Método | Endpoint | Descrição |
|---------|----------|-------------|
| GET | `/users` | Retorna todos os usuários |
| GET | `/users/{id}` | Retorna um usuário pelo ID |
| POST | `/users` | Cria um novo usuário |
| PUT | `/users/{id}` | Atualiza um usuário existente |
| DELETE | `/users/{id}` | Remove um usuário pelo ID |

## Exemplo de Requisição

### Criar um Usuário
```sh
curl -X POST "http://localhost:8080/api/users" \
     -H "Content-Type: application/json" \
     -d '{"name": "João Silva", "email": "joao@email.com"}' \
     -u admin:admin
```

## Considerações Finais
Este projeto é um exemplo simples para estudos sobre CRUD em Java com Spring Boot, banco de dados H2 e autenticação básica. Sinta-se à vontade para melhorá-lo e expandi-lo conforme necessário!
