package com.mpt.journal.service;

import com.mpt.journal.dto.PageResponse;
import com.mpt.journal.entity.Profile;

public interface ProfileService {
    Profile createProfile(Profile profile);
    Profile getProfileById(Long id);
    Profile getProfileByUserId(Long userId);
    PageResponse<Profile> getAllProfiles(int page, int size);
    Profile updateProfile(Long id, Profile profile);
    void deleteProfile(Long id);
}