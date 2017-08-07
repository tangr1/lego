package com.github.tangr1.lego.auth.repository;

import com.github.tangr1.lego.auth.entity.Client;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends CrudRepository<Client, Long> {

    Client findByClientId(final String client);
}
