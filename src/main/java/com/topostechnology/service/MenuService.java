package com.topostechnology.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.topostechnology.domain.Menu;
import com.topostechnology.domain.Role;
import com.topostechnology.repository.MenuRepository;
import com.topostechnology.repository.RoleRepository;

@Service
public class MenuService {

    @Resource
    private MenuRepository menuRepository;

    @Resource
    private RoleRepository roleRepository;

    public List<Menu> getMenusByRole(Role role) {
        Optional<Role> optionalRole = roleRepository.findById(role.getId());
        if (optionalRole.isPresent()) {
            return role.getMenus().stream().sorted(Comparator.comparingInt(Menu::getOrder)).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    public List<Menu> getParentMenus() {
        return menuRepository.findByParent(null);
    }
}
