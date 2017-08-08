package com.github.tangr1.lego.company.repository;

import com.github.tangr1.lego.company.entity.Company;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends PagingAndSortingRepository<Company, Long> {
}
