# Tarefas API

API REST para gerenciamento de tarefas, com interface web simples.

**Stack:** Java 25, Spring Boot, Spring Data JPA, Bean Validation, PostgreSQL 17, H2, Docker e Docker Compose.

## Como executar

Pré-requisito: Docker instalado.

**1. Back-end (API + banco)**

```bash
docker compose up -d --build
```

A API sobe em `http://localhost:8080`. Os dados ficam no volume `pgdata` e sobrevivem a reinícios.

Para parar (os dados são mantidos):

```bash
docker compose down
```

**2. Front-end**

```bash
cd frontend
python -m http.server 5500
```

Acesse `http://localhost:5500`. O front precisa ser aberto por `localhost` (a API libera CORS apenas para `http://localhost:*`).

## Endpoints

| Método | Rota | Descrição | Sucesso |
|--------|------|-----------|---------|
| GET | `/tarefa` | Lista tarefas (`?q=` filtra por nome, aceita paginação) | 200 |
| GET | `/tarefa/{id}` | Consulta uma tarefa | 200 |
| POST | `/tarefa` | Cria tarefa | 201 |
| PUT | `/tarefa/{id}` | Altera tarefa | 200 |
| DELETE | `/tarefa/{id}` | Exclui tarefa | 204 |
| PATCH | `/tarefa/{id}/concluir` | Conclui tarefa | 200 |
| PATCH | `/tarefa/{id}/reabrir` | Reabre tarefa | 200 |

Exemplo de criação:

```bash
curl -X POST -H "Content-Type: application/json" \
  -d '{"nome":"Estudar Java","dataVencimento":"2026-12-31"}' \
  http://localhost:8080/tarefa
```

## Regras de negócio

- Nome obrigatório, até 60 caracteres.
- Data de vencimento obrigatória e não pode estar no passado.
- Ao alterar, a nova data não pode ser anterior à data atual da tarefa.
- Não é possível concluir uma tarefa já concluída, nem reabrir uma que não está concluída.
- Em ambiente `PRODUCAO` (variável `AMBIENTE`), tarefa concluída não pode ser excluída.

## Tratamento de erros

Todos os erros seguem o mesmo formato (`ErrorDTO`):

```json
{
  "status": 400,
  "mensagem": "Dados inválidos",
  "campos": { "nome": "não deve estar em branco" }
}
```

O campo `campos` só aparece em erros de validação. Cobre: validação (400), regra de negócio (400), JSON malformado (400), tarefa inexistente (404), rota inexistente (404) e erro interno (500).

## Arquitetura

Camadas: `Controller` (HTTP) → `Service` (regras de negócio) → `Repository` (Spring Data JPA) → PostgreSQL.

- **DTOs** validam o formato da entrada; o **Service** valida o que depende do banco ou do estado da tarefa.
- Um único `ControllerAdvice` trata todas as exceções.

## Perfis e infraestrutura

- Sem profile: usa H2 em memória (os dados se perdem ao reiniciar).
- Profile `docker`: usa PostgreSQL.
- Dockerfile multi-stage (build com JDK e Maven, execução apenas com JRE).
- Docker Compose com healthcheck: a API só inicia quando o banco está pronto.
## Testes

```bash
mvn test
```

## Melhorias futuras

- Migrações de banco (Flyway ou Liquibase) no lugar de `ddl-auto=update`.
- Credenciais do banco por variáveis de ambiente ou segredos.
- Autenticação.
- Listagem paginada com total de páginas.
- Mais testes automatizados (controllers e DTOs).