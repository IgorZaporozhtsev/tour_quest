package com.tour.quest.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.Set;

@Entity
@Data
@Table(name = "usr")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank(message = "Username cannot be empty")
    private String username;
    @NotBlank(message = "Password cannot be empty")
    private String password;

    @Transient //hibernate do not mapping this field
    @NotBlank(message = "Password conformation cannot be empty")
    //this filed need for auto validation
    private String passwordValidator;


    private boolean active;

    @Email(message = "Email is not correct")
    @NotBlank(message = "Email cannot be empty")
    private String email;
    private String activationCode;

    //означает что User содержит колекцию обьектов которые не являються сущностью
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    //настраивание деталей таблицы колекции @JoinColumn(name = "user_id") для связывание с id user-a
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive();
    }

    public String getPasswordValidator() {
        return passwordValidator;
    }

    public void setPasswordValidator(String passwordValidator) {
        this.passwordValidator = passwordValidator;
    }
}
