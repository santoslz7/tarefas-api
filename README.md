# Tarefas API

API REST para gerenciamento de tarefas. Permite criar, consultar, alterar, excluir, concluir e reabrir tarefas, com validação dos dados de entrada e respostas de erro padronizadas. Os dados são persistidos em PostgreSQL, e a aplicação roda em containers Docker.

## Tecnologias

- Java 25 e Spring Boot
- Spring Data JPA e Bean Validation
- PostgreSQL 17
- H2 (execução local sem Docker)
- Docker e Docker Compose

## Como executar

Pré-requisito: Docker Desktop aberto.

```bash
docker compose up -d --build
```

A API sobe em `http://localhost:8080`. Os dados ficam guardados no volume `pgdata`.

Para parar (os dados são mantidos):

```bash
docker compose down
```

### Sem Docker

Execute a classe `TarefasApplication` pelo IDE. Sem profile ativo, a aplicação usa o banco H2 em memória.