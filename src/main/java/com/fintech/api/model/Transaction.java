package com.fintech.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    private String description;

    @Column(nullable = false)
    private BigDecimal balanceAfter;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }

    public Transaction(Account account, TransactionType type, BigDecimal amount, String description) {
        this.account = account;
        this.type = type;
        this.amount = amount;
        this.description = description;
    }

    public Transaction(Account account, TransactionType type, BigDecimal amount, String description, BigDecimal balanceAfter) {
        this.account = account;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.balanceAfter = balanceAfter;
    }
}
