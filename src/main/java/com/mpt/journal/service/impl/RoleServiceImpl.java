package com.mpt.journal.service.impl;

import com.mpt.journal.entity.Role;
import com.mpt.journal.exception.ResourceNotFoundException;
import com.mpt.journal.repository.RoleRepository;
import com.mpt.journal.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    @Transactional(readOnly = true)
    public Role getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Role getRoleByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + name));
    }

    // ИСПРАВЛЕНО: Добавлен метод getAllRoles(), который возвращает List<Role>
    @Override
    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Role> getAllRoles(int page, int size) {
        return roleRepository.findAll(PageRequest.of(page, size));
    }

    @Override
    public Role updateRole(Long id, Role role) {
        Role existing = getRoleById(id);
        existing.setName(role.getName());
        return roleRepository.save(existing);
    }

    @Override
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role not found with id: " + id);
        }
        roleRepository.deleteById(id);
    }

    @Override
    public void softDeleteRole(Long id) {
        // Role не имеет isDeleted, поэтому не поддерживается
        throw new UnsupportedOperationException("Soft delete not supported for Role");
    }

    @Override
    public void deleteRoles(List<Long> ids) {
        ids.forEach(this::deleteRole);
    }
}