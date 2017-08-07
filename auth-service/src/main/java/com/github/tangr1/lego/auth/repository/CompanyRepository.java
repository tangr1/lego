package com.github.tangr1.lego.auth.repository;

import com.github.tangr1.lego.auth.entity.Company;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends PagingAndSortingRepository<Company, Long> {
}
