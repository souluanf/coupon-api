# Desafio técnico

Crie um projeto que implemente os endpoints definidos na documentação, seguindo as regras de negócio estabelecidas.

**Documentação da API.**
[/coupon - Coupon Api](coupon.openapi.json)

## Regras de Negócio

### Create
* Um cupom pode ser cadastrado a qualquer momento e possui como obrigatórios os campos:
    * `code`
    * `description`
    * `discountValue`
    * `expirationDate`
* O código de um cupom é um campo alfanumérico com um tamanho padrão de 6 caracteres.
  * Caracteres especiais podem ser aceitos na criação, contudo precisam ser removidos pela aplicação antes de salvar e retornar na resposta, garantindo o tamanho de 6 caracteres.
* O valor de desconto do cupom possui um saldo mínimo de 0,5 sem máximo predeterminado. (Saldo é absoluto e não há preocupações com moeda.)
* O cupom nunca pode ser criado com data de expiração no passado.
* Um cupom pode ser criado como já publicado.

### Delete
* Um cupom pode ser deletado a qualquer momento.
* Deve ser feito um *soft delete* do cupom no banco de dados, garantindo a não perda de informações recebidas no cadastro.
* Não deve ser possível deletar um cupom já deletado.

## Expectativas
* Testes cobrindo as regras de negócio (80%).
* Utilização de banco em memória H2.
* Publicação do projeto no GitHub com repositório público.
* As regras de negócio devem estar encapsuladas em objetos de domínio. (Domínio é diferente de entidade JPA)
* Docker e Docker Compose.
* Swagger.