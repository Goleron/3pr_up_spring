package com.mpt.journal.service;

import com.mpt.journal.entity.User;
import com.mpt.journal.dto.PageResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    User createUser(User user);
    User getUserById(Long id);
    PageResponse<User> getAllUsers(int page, int size, String search);
    User updateUser(Long id, User user);
    void deleteUser(Long id);
    void softDeleteUser(Long id);
    void deleteUsers(List<Long> ids);
}