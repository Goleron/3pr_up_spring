package com.mpt.journal.service.impl;

import com.mpt.journal.dto.PageResponse;
import com.mpt.journal.entity.Profile;
import com.mpt.journal.exception.ResourceNotFoundException;
import com.mpt.journal.repository.ProfileRepository;
import com.mpt.journal.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;

    @Override
    public Profile createProfile(Profile profile) {
        return profileRepository.save(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public Profile getProfileById(Long id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Profile getProfileByUserId(Long userId) {
        return profileRepository.findByUserId(userId).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<Profile> getAllProfiles(int page, int size) {
        PageRequest pr = PageRequest.of(page, size, Sort.by("id"));
        Page<Profile> result = profileRepository.findAll(pr);
        return toPageResponse(result);
    }

    @Override
    public Profile updateProfile(Long id, Profile profile) {
        Profile existing = getProfileById(id);
        existing.setFirstName(profile.getFirstName());
        existing.setLastName(profile.getLastName());
        existing.setPhone(profile.getPhone());
        existing.setAddress(profile.getAddress());
        return profileRepository.save(existing);
    }

    @Override
    public void deleteProfile(Long id) {
        if (!profileRepository.existsById(id)) {
            throw new ResourceNotFoundException("Profile not found with id: " + id);
        }
        profileRepository.deleteById(id);
    }

    private <T> PageResponse<T> toPageResponse(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}