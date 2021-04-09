package com.vydra.transactionservice.persistence.repositories;

import com.vydra.transactionservice.persistence.model.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {
}