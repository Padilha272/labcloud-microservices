# LabCloud Microservices

Arquitetura de microsserviços da plataforma LabCloud.

## Serviços

| Serviço | Porta | Responsabilidade | Banco |
|---------|-------|------------------|-------|
| **Auth Service** | 8081 | Laboratórios, Usuários, Autenticação | `labcloud_auth` |
| **Sample Service** | 8082 | Experimentos, Amostras | `labcloud_sample` |
| **Result Service** | 8083 | Resultados | `labcloud_result` |

## Tecnologias

- Spring Boot 3.2.5
- PostgreSQL
- Spring Security + JWT
- OpenFeign (comunicação)
- Maven

## Como rodar

### Pré-requisitos
- Java 17+
- PostgreSQL rodando em `localhost:5432`
- 3 bancos criados

# Terminal 1
cd auth-service
mvn spring-boot:run

# Terminal 2
cd sample-service
mvn spring-boot:run

# Terminal 3
cd result-service
mvn spring-boot:run



### Criar bancos

```sql
CREATE DATABASE labcloud_auth;
CREATE DATABASE labcloud_sample;
CREATE DATABASE labcloud_result;