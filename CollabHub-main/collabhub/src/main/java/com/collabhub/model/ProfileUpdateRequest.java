package com.collabhub.model;

import lombok.Data;

@Data
public class ProfileUpdateRequest {
    private String email;
    private String newPassword;
}
