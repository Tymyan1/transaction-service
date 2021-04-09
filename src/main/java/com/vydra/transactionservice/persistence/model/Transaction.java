package com.vydra.transactionservice.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Currency;

@Entity
@Table(name = "transactions")
@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
@NoArgsConstructor
public class Transaction extends BaseEntity {

    @Column(name = "sourceAccountId", nullable = false)
    private Long sourceAccountId;
    @Column(name = "targetAccountId", nullable = false)
    private Long targetAccountId;
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;
    @Column(name = "currency", nullable = false)
    private Currency currency;
}
