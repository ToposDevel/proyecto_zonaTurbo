package com.topostechnology.model;


import lombok.Data;

@Data
public class ResetPasswordFormModel {
    private Long userId;
    private String username;
    private String newPassword;
    private String confirmNewPassword;
    private Long storeId;
    private String storeName;
}
