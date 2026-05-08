# Coupon API

REST API para gerenciamento de cupons de desconto, com ciclo de vida completo: criação, publicação, resgate e exclusão lógica.

---

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=souluanf_coupon-api&metric=alert_status&token=0b8cc8af8f76bcde0ca74ea643be90b74ca6187a)](https://sonarcloud.io/summary/new_code?id=souluanf_coupon-api)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=souluanf_coupon-api&metric=coverage&token=0b8cc8af8f76bcde0ca74ea643be90b74ca6187a)](https://sonarcloud.io/summary/new_code?id=souluanf_coupon-api)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=souluanf_coupon-api&metric=code_smells&token=0b8cc8af8f76bcde0ca74ea643be90b74ca6187a)](https://sonarcloud.io/summary/new_code?id=souluanf_coupon-api)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=souluanf_coupon-api&metric=ncloc&token=0b8cc8af8f76bcde0ca74ea643be90b74ca6187a)](https://sonarcloud.io/summary/new_code?id=souluanf_coupon-api)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=souluanf_coupon-api&metric=sqale_index&token=0b8cc8af8f76bcde0ca74ea643be90b74ca6187a)](https://sonarcloud.io/summary/new_code?id=souluanf_coupon-api)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=souluanf_coupon-api&metric=reliability_rating&token=0b8cc8af8f76bcde0ca74ea643be90b74ca6187a)](https://sonarcloud.io/summary/new_code?id=souluanf_coupon-api)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=souluanf_coupon-api&metric=duplicated_lines_density&token=0b8cc8af8f76bcde0ca74ea643be90b74ca6187a)](https://sonarcloud.io/summary/new_code?id=souluanf_coupon-api)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=souluanf_coupon-api&metric=vulnerabilities&token=0b8cc8af8f76bcde0ca74ea643be90b74ca6187a)](https://sonarcloud.io/summary/new_code?id=souluanf_coupon-api)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=souluanf_coupon-api&metric=bugs&token=0b8cc8af8f76bcde0ca74ea643be90b74ca6187a)](https://sonarcloud.io/summary/new_code?id=souluanf_coupon-api)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=souluanf_coupon-api&metric=security_rating&token=0b8cc8af8f76bcde0ca74ea643be90b74ca6187a)](https://sonarcloud.io/summary/new_code?id=souluanf_coupon-api)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=souluanf_coupon-api&metric=sqale_rating&token=0b8cc8af8f76bcde0ca74ea643be90b74ca6187a)](https://sonarcloud.io/summary/new_code?id=souluanf_coupon-api)

---

## Stack

[![Java 25](https://img.shields.io/badge/Java-25-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://docs.oracle.com/en/java/javase/25/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.6-6DB33F?style=flat-square&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MapStruct](https://img.shields.io/badge/MapStruct-1.6.3-22509A?style=flat-square&logo=mapstruct&logoColor=white)](https://mapstruct.org/)
[![Flyway](https://img.shields.io/badge/Flyway-11.11.2-22509A?style=flat-square&logo=flyway&logoColor=white)](https://flywaydb.org/)
[![Swagger](https://img.shields.io/badge/Swagger-3.1.0-85EA2D?style=flat-square&logo=swagger&logoColor=black)](https://swagger.io/)
[![Kubernetes](https://img.shields.io/badge/Kubernetes-1.26-326CE5?style=flat-square&logo=kubernetes&logoColor=white)](https://kubernetes.io/)
[![H2](https://img.shields.io/badge/H2-2.2.224-22509A?style=flat-square&logo=h2&logoColor=white)](https://www.h2database.com/html/main.html)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18-4479A1?style=flat-square&logo=postgresql&logoColor=white)](https://www.postgresql.org/docs/)
[![Docker](https://img.shields.io/badge/Docker-enabled-2496ED?style=flat-square&logo=docker&logoColor=white)](https://docs.docker.com)

---

## Quick Start

### Local (H2 em memória)

Requer apenas Java 25:

```bash
./mvnw spring-boot:run
```

### Docker Compose (PostgreSQL)

```bash
cp .env.example .env
docker compose up --build
```

API: `http://localhost:8080` · Swagger: `http://localhost:8080/swagger-ui/index.html`

---

## API Reference

O payload de criação inclui o campo `published`, que define o estado inicial do cupom. Esse campo pressupõe que o estado de publicação pode mudar ao longo do tempo — o que torna `/publish` e `/redeem` extensões naturais do modelo. O endpoint de listagem foi incluído para facilitar a inspeção dos dados durante o desenvolvimento.

| Método | Rota | Status | Descrição |
|---|---|---|---|
| `POST` | `/coupon` | 201 | Criar cupom |
| `GET` | `/coupon/{id}` | 200 | Buscar por ID |
| `DELETE` | `/coupon/{id}` | 204 | Soft delete |
| `PATCH` | `/coupon/{id}/publish` | 200 | Publicar (INACTIVE → ACTIVE) |
| `PATCH` | `/coupon/{id}/redeem` | 200 | Resgatar cupom |
| `GET` | `/coupon` | 200 | Listar com filtro e paginação |

### Exemplos

#### Criar

```bash
curl -s -X POST http://localhost:8080/coupon \
  -H "Content-Type: application/json" \
  -d '{"code":"ABC-123","description":"20% off","discountValue":0.8,"expirationDate":"2027-12-31T23:59:59","published":false}'
```

#### Buscar por ID

```bash
curl -s http://localhost:8080/coupon/{id}
```

#### Publicar

```bash
curl -s -X PATCH http://localhost:8080/coupon/{id}/publish
```

#### Resgatar

```bash
curl -s -X PATCH http://localhost:8080/coupon/{id}/redeem
```

#### Soft delete

```bash
curl -s -X DELETE http://localhost:8080/coupon/{id}
```

#### Listar

```bash
# Todos (exclui deletados por padrão)
curl -s http://localhost:8080/coupon

# Apenas ativos
curl -s "http://localhost:8080/coupon?status=ACTIVE"

# Apenas deletados
curl -s "http://localhost:8080/coupon?status=DELETED"
```

A listagem suporta paginação (`page`, `size`) e ordenação (`sort`).

---

## Regras de Negócio

### Código

O campo `code` é alfanumérico com exatamente **6 caracteres**:

- Caracteres especiais são removidos antes de salvar (`ABC-123` → `ABC123`)
- Letras são convertidas para maiúsculas
- Se após sanitização restar menos de 6 caracteres, a criação é rejeitada com `400`

### Desconto

- Valor mínimo: **0.5** (absoluto, sem unidade monetária)

### Data de expiração

- Não pode ser no passado — validado no momento da criação

### Unicidade do código

Um código não pode ser reutilizado por dois cupons ativos simultaneamente. A proteção é dupla:

**1. Nível de aplicação** — verifica antes de salvar:

```java
repository.existsByCodeAndStatusNot(code, CouponStatus.DELETED)
```

**2. Nível de banco (PostgreSQL)** — índice único parcial via Flyway `V2`:

```sql
CREATE UNIQUE INDEX uq_coupon_code_active ON coupon(code) WHERE status <> 'DELETED';
```

> O índice `V2` existe apenas nos perfis PostgreSQL (`dev`/`prd`). No H2 (`local`/`test`) a proteção é garantida exclusivamente pela validação de aplicação, pois H2 não suporta índices parciais.

### Reutilização após soft delete

Cupons deletados liberam o código para reutilização. Tanto o check de aplicação quanto o índice parcial contemplam esse comportamento — a restrição ignora registros com `status = 'DELETED'`.

### Soft delete

A exclusão não remove o registro. O `status` é alterado para `DELETED` e `deleted_at` é preenchido com o timestamp da operação. Os dados originais são preservados integralmente.

Tentar deletar um cupom já deletado retorna `409 Conflict`.

---

## Arquitetura

O projeto separa explicitamente **objeto de domínio** de **entidade JPA**:

```
CouponRequest (DTO)
    ↓
CouponDomain (domínio puro — validações e sanitização)
    ↓
Coupon (entidade JPA — persistência)
    ↓
CouponResponse (DTO)
```

`CouponDomain` encapsula todas as regras de negócio sem dependências de framework. Sanitização do código, desconto mínimo e data de expiração vivem exclusivamente nessa classe.

---

## Testes

```bash
./mvnw test
```

| Classe | Tipo | Cobertura |
|---|---|---|
| `CouponDomainTest` | Unitário | Domínio puro |
| `CouponControllerTest` | Integração (H2) | Endpoints |
| `ExceptionHandlerAdviceTest` | Unitário | Handlers de exceção |
| `ArchitectureTest` | ArchUnit | Regras arquiteturais |

---

## Perfis

| Perfil | Banco | Uso |
|---|---|---|
| `local` | H2 em memória | Desenvolvimento local |
| `dev` | PostgreSQL | Ambiente de desenvolvimento |
| `prd` | PostgreSQL | Produção |
| `test` | H2 em memória | Testes automatizados |