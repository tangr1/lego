package com.github.tangr1.lego.auth.repository;

import com.github.tangr1.lego.auth.entity.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<Account, Integer> {

    Account findByPhone(final String phone);

    Account findByName(final String name);
}
