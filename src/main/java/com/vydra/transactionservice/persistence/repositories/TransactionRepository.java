package com.vydra.transactionservice.persistence.repositories;

import com.vydra.transactionservice.persistence.model.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {
}