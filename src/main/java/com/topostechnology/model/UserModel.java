package com.topostechnology.model;

import java.util.Date;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class UserModel {

    private Long id;
    private String name;
    private String lastName;
    private String secondLastName;
    private String cellphoneNumber;
    private String password;
    private String passwordConfirmation;
    private String email;
    private int role;
    private boolean resetPassword;
    private Date passwordUpdatedAt;
    private boolean saved;
    private boolean updatePassword;
    private boolean active;
    private String userName;
    private String roleName;
    private boolean loginWithImei;
    private String imei;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserModel)) return false;
        UserModel user = (UserModel) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
