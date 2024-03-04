package com.topostechnology.domain;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user")
@Getter
@Setter
public class User extends CoreCatalogEntity {

	private static final long serialVersionUID = 8353607814691639442L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Column(name = "name", nullable = false, length = 30)
    private String name;

    @Column(name = "last_name", nullable = false, length = 36)
    private String lastName;
    
    @Column(name = "second_last_name", nullable = false, length = 36)
    private String secodLastName;
    
    @Column(name = "email", nullable = false, length = 50)
    private String email;
    
    @Column(name = "user_name", nullable = false, length = 20, unique = true)
    private String userName;
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user")
    @Fetch(value = FetchMode.SUBSELECT)
    private  List<Phone> phones;
    
    @Column(name = "password", nullable = false, length = 20)
    private String password;
    
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name = "reset_password")
    private boolean resetPassword;

    @Column(name = "password_updated_at")
    private Date passwordUpdatedAt;
    
    @Column(name = "login_imei")
    private boolean loginWithImei;
    
    @Column(name="registered_in_acrobits")
    private boolean registeredInacrobits;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
