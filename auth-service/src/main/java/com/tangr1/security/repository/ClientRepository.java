package com.tangr1.security.repository;

import com.tangr1.security.entity.Client;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends CrudRepository<Client, Long> {

    Client findByClientId(final String client);
}
