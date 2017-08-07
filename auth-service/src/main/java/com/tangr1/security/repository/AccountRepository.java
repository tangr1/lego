package com.tangr1.security.repository;

import com.tangr1.security.entity.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<Account, Integer> {

    Account findByPhone(final String phone);
}
