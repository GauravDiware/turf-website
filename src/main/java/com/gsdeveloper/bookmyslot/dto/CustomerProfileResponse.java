package com.gsdeveloper.bookmyslot.dto;

import com.gsdeveloper.bookmyslot.entity.Profile;
import com.gsdeveloper.bookmyslot.entity.User;

public record CustomerProfileResponse(String fullName, String email, String mobile, String avatarUrl) {
    public static CustomerProfileResponse from(User user, Profile profile) {
        return new CustomerProfileResponse(user.getFullName(), user.getEmail(), user.getMobile(), profile.getAvatarUrl());
    }
}
