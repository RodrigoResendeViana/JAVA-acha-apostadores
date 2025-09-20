# Estratégias de Migração de Banco de Dados

## Visão Geral
Este projeto usa diferentes estratégias para gerenciar o schema do banco de dados dependendo do ambiente.

## Estratégias Disponíveis

### 1. Schema.sql Único (Atual)
- **Arquivo**: `src/main/resources/schema.sql`
- **Vantagens**: Simples, controle total, versionado no Git
- **Desvantagens**: Manual, sem controle de versão automática
- **Uso**: Desenvolvimento e produção

### 2. Múltiplos Arquivos Schema
- **Arquivos**: `schema.sql`, `schema-v1.1.sql`, `schema-v1.2.sql`, etc.
- **Configuração**:
  ```properties
  spring.sql.init.schema-locations=classpath*:schema*.sql
  ```
- **Vantagens**: Versionamento incremental, histórico claro
- **Uso**: Quando precisar de mudanças incrementais

### 3. Hibernate DDL Auto
- **Configurações**:
  - `validate`: Apenas valida se o schema existe (produção)
  - `update`: Cria/atualiza tabelas automaticamente (desenvolvimento)
  - `create-drop`: Recria tabelas a cada restart (teste)
- **Vantagens**: Automático, baseado nas entidades JPA
- **Desvantagens**: Sem controle fino, pode perder dados

## Como Escolher

### Para Desenvolvimento
- Use `schema.sql` único para setup inicial
- Ou `spring.jpa.hibernate.ddl-auto=update` para mudanças rápidas

### Para Produção
- Prefira `schema.sql` versionado
- Ou Flyway/Liquibase para controle rigoroso de versões

## Boas Práticas

1. **Sempre use `IF NOT EXISTS`** em CREATE TABLE
2. **Sempre use `IF NOT EXISTS`** em CREATE INDEX
3. **Documente as mudanças** com comentários
4. **Versione os arquivos** no Git
5. **Teste as migrações** em ambiente de desenvolvimento primeiro

## Exemplos de Mudanças

### Adicionando uma coluna:
```sql
ALTER TABLE users ADD COLUMN IF NOT EXISTS phone VARCHAR(20);
```

### Adicionando um índice:
```sql
CREATE INDEX IF NOT EXISTS idx_users_phone ON users(phone);
```

### Modificando uma coluna:
```sql
ALTER TABLE users ALTER COLUMN name TYPE VARCHAR(300);
```