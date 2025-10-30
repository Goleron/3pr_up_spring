package com.mpt.journal.service;

import com.mpt.journal.entity.Role;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RoleService {
    List<Role> getAllRoles();
    Role createRole(Role role);
    Role getRoleById(Long id);
    Role getRoleByName(String name);
    Page<Role> getAllRoles(int page, int size);
    Role updateRole(Long id, Role role);
    void deleteRole(Long id); // физическое
    void softDeleteRole(Long id); // логическое
    void deleteRoles(List<Long> ids);
}