package org.example.be.service.impl;

import org.example.be.model.Role;
import org.example.be.respository.RoleRespository;
import org.example.be.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceIMPL implements RoleService {
    @Autowired
    private RoleRespository roleRespository;

    @Override
    public Iterable<Role> findAll() {
        return roleRespository.findAll();
    }

    @Override
    public void save(Role role) {
        roleRespository.save(role);
    }

    @Override
    public Role findByName(String name) {
        return roleRespository.findByName(name);
    }
}
