package com.collabhub.model;


import lombok.Data;

import java.util.List;

@Data
public class UserProfileResponse {
    private Long id;
    private String username;
    private String email;
    private String role;
    private List<String> projectNames;
}
