# Persistência Futura

Este diretório será usado para interfaces de Repository quando Spring Data JPA for adicionado ao projeto.

## Próximos passos para persistência

### 1. Adicionar Spring Data JPA ao pom.xml

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

### 2. Criar Repository para PixTransaction

```java
@Repository
public interface PixTransactionRepository extends JpaRepository<PixTransaction, String> {
    Optional<PixTransaction> findByTxid(String txid);
}
```

### 3. Anotar Entity com JPA

```java
@Entity
@Table(name = "pix_transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PixTransaction {
    
    @Id
    private String txid;
    
    @Column(nullable = false)
    private String status;
    
    // ... demais campos ...
}
```

### 4. Usar Repository em Services

```java
pixTransactionRepository.save(transaction);
pixTransactionRepository.findByTxid(txid);
```

## Estrutura de Bancos Recomendados

- **Desenvolvimento**: H2 (em memória)
- **Teste**: H2 (em memória)
- **Produção**: PostgreSQL ou MySQL
