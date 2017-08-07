package com.tangr1.security.controller;

import com.tangr1.security.entity.Company;
import com.tangr1.security.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/companies")
public class CompanyController {

    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private MutableAclService mutableAclService;

    @GetMapping
    public Iterable<Company> list() {
        return companyRepository.findAll();
    }

    @PostMapping
    @Transactional
    public Company create(final @RequestBody Company request) {
        Company company = companyRepository.save(request);
        ObjectIdentity identity = new ObjectIdentityImpl(company);
        MutableAcl acl = mutableAclService.createAcl(identity);

        acl.insertAce(acl.getEntries().size(), BasePermission.ADMINISTRATION,
                new PrincipalSid(SecurityContextHolder.getContext().getAuthentication()),
                true);

        mutableAclService.updateAcl(acl);
        return company;
    }

    @DeleteMapping("/{id}")
    @Transactional
    @PreAuthorize("hasPermission(#id, 'com.tangr1.security.domain.Company', 'administration')")
    public void delete(final @PathVariable Long id) {
        Company company = companyRepository.findOne(id);
        ObjectIdentity identity = new ObjectIdentityImpl(company);
        mutableAclService.deleteAcl(identity, true);
        companyRepository.delete(id);
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public Company get(final @PathVariable Long id) {
        return companyRepository.findOne(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasPermission(#id, 'com.tangr1.security.domain.Company', 'administration')")
    @Transactional
    public Company update(final @PathVariable Long id, final @RequestBody Company request) {
        Company company = companyRepository.findOne(id);
        company.setName(request.getName());
        return companyRepository.save(company);
    }
}
