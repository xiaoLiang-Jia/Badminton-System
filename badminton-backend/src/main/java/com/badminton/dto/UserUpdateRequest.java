package com.badminton.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String realName;
    private String phone;
    private String email;
    private String avatar;
}
