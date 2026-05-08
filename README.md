# Coupon API

REST API para gerenciamento de cupons de desconto.

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
[![SonarQube Cloud](https://sonarcloud.io/images/project_badges/sonarcloud-dark.svg)](https://sonarcloud.io/summary/new_code?id=souluanf_coupon-api)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=souluanf_coupon-api&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=souluanf_coupon-api)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=souluanf_coupon-api&metric=coverage)](https://sonarcloud.io/summary/new_code?id=souluanf_coupon-api)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=souluanf_coupon-api&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=souluanf_coupon-api)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=souluanf_coupon-api&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=souluanf_coupon-api)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=souluanf_coupon-api&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=souluanf_coupon-api)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=souluanf_coupon-api&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=souluanf_coupon-api)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=souluanf_coupon-api&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=souluanf_coupon-api)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=souluanf_coupon-api&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=souluanf_coupon-api)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=souluanf_coupon-api&metric=bugs)](https://sonarcloud.io/summary/new_code?id=souluanf_coupon-api)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=souluanf_coupon-api&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=souluanf_coupon-api)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=souluanf_coupon-api&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=souluanf_coupon-api)


--

## Como rodar

### Sem Docker (H2 em memória)

Requer apenas Java 25 instalado:

```bash
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080`.  
Swagger: `http://localhost:8080/swagger-ui/index.html`

### Com Docker Compose (PostgreSQL)

```bash
cp .env.example .env
docker compose up --build
```

A API estará disponível em `http://localhost:8080`.  
Swagger: `http://localhost:8080/swagger-ui/index.html`

---

## Endpoints

| Método | Rota | Status | Descrição |
|---|---|---|---|
| `POST` | `/coupon` | 201 | Criar cupom |
| `GET` | `/coupon/{id}` | 200 | Buscar cupom por ID |
| `DELETE` | `/coupon/{id}` | 204 | Soft delete |
| `PATCH` | `/coupon/{id}/publish` | 200 | Publicar cupom |
| `PATCH` | `/coupon/{id}/redeem` | 200 | Resgatar cupom |
| `GET` | `/coupon` | 200 | Listar cupons (extra) |

### Exemplos

```bash
# Criar cupom
curl -s -X POST http://localhost:8080/coupon \
  -H "Content-Type: application/json" \
  -d '{"code":"ABC-123","description":"20% off","discountValue":0.8,"expirationDate":"2027-12-31T23:59:59","published":false}'

# Buscar por ID
curl -s http://localhost:8080/coupon/{id}

# Deletar
curl -s -X DELETE http://localhost:8080/coupon/{id}

# Listar (exclui deletados por padrão)
curl -s http://localhost:8080/coupon

# Listar apenas deletados
curl -s "http://localhost:8080/coupon?status=DELETED"

# Publicar cupom (INACTIVE → ACTIVE)
curl -s -X PATCH http://localhost:8080/coupon/{id}/publish

# Resgatar cupom
curl -s -X PATCH http://localhost:8080/coupon/{id}/redeem
```

---

## Regras de Negócio

### Código do cupom

O campo `code` é alfanumérico com exatamente **6 caracteres**:

- Caracteres especiais são removidos automaticamente antes de salvar (`ABC-123` → `ABC123`)
- Letras são convertidas para maiúsculas
- Se após sanitização restar menos de 6 caracteres alfanuméricos, a criação é rejeitada com 400

### Desconto

- Valor mínimo: **0.5** (absoluto, sem unidade monetária)
- Sem máximo predeterminado

### Data de expiração

- Não pode ser no passado — validado no momento da criação

### Unicidade do código

Um código não pode ser reutilizado por dois cupons ativos ao mesmo tempo. A proteção é dupla:

**1. Nível de aplicação** — antes de salvar, verifica se já existe cupom com o mesmo código e status diferente de `DELETED`:

```java
repository.existsByCodeAndStatusNot(code, CouponStatus.DELETED)
```

**2. Nível de banco (PostgreSQL)** — índice único parcial aplicado via Flyway na migration `V2`:

```sql
CREATE UNIQUE INDEX uq_coupon_code_active ON coupon(code) WHERE status <> 'DELETED';
```

O índice parcial garante que a restrição só se aplica a cupons não deletados, funcionando como camada de segurança adicional ao check de aplicação.

> O índice V2 existe apenas no perfil PostgreSQL (`dev`/`prd`). No H2 (`local`/`test`) a proteção é garantida exclusivamente pela validação de aplicação, pois H2 não suporta índices parciais.

### Reutilização após soft delete

Se um cupom foi deletado (soft delete), seu código fica livre para ser reutilizado em um novo cadastro. Tanto o check de aplicação quanto o índice parcial contemplam esse comportamento — a restrição ignora registros com `status = 'DELETED'`.

### Soft delete

A exclusão não remove o registro do banco. O cupom tem seu `status` alterado para `DELETED` e o campo `deleted_at` é preenchido com o timestamp da exclusão. Os dados originais são preservados integralmente.

Tentar deletar um cupom já deletado retorna `409 Conflict`.

---

## Listagem (GET /coupon)

> Este endpoint não faz parte da especificação original do desafio. Foi adicionado para facilitar a visualização das massas de dados durante o desenvolvimento e validação dos comportamentos de soft delete e filtro por status.

Por padrão, a listagem **exclui cupons deletados**. Use o parâmetro `status` para filtrar explicitamente:

| Parâmetro | Exemplo | Comportamento |
|---|---|---|
| _(sem status)_ | `GET /coupon` | Retorna ACTIVE e INACTIVE |
| `status=ACTIVE` | `GET /coupon?status=ACTIVE` | Apenas ativos |
| `status=DELETED` | `GET /coupon?status=DELETED` | Apenas deletados |

Suporta paginação (`page`, `size`) e ordenação (`sort`).

---

## Arquitetura

O projeto separa explicitamente **objeto de domínio** de **entidade JPA**, conforme solicitado no desafio:

```
CouponRequest (DTO)
    ↓
CouponDomain (domínio puro — validações e sanitização)
    ↓
Coupon (entidade JPA — persistência)
    ↓
CouponResponse (DTO)
```

`CouponDomain` encapsula todas as regras de negócio sem dependências de framework. As validações de sanitização do código, desconto mínimo e data de expiração vivem exclusivamente nessa classe.

---

## Testes

```bash
./mvnw test
```

- `CouponDomainTest` — testes unitários do domínio puro
- `CouponControllerTest` — testes de integração com H2 em memória
- `ExceptionHandlerAdviceTest` — testes unitários do handler de exceções
- `ArchitectureTest` — regras arquiteturais via ArchUnit

---

## Perfis

| Perfil | Banco | Uso |
|---|---|---|
| `local` | H2 em memória | Desenvolvimento local |
| `dev` | PostgreSQL | Ambiente de desenvolvimento |
| `prd` | PostgreSQL | Produção |
| `test` | H2 em memória | Testes automatizados |
