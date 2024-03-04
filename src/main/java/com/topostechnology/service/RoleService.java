package com.topostechnology.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.topostechnology.domain.Role;
import com.topostechnology.repository.BaseRepository;
import com.topostechnology.repository.RoleRepository;

@Service
public class RoleService extends CoreCatalogService<Role> {

    private RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        super("Rol");
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    protected BaseRepository<Role, Long> getRepository() {
        return roleRepository;
    }

}
