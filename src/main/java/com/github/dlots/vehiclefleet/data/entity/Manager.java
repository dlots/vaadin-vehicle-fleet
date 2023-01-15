package com.github.dlots.vehiclefleet.data.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Manager extends AbstractEntity implements UserDetails {
    public static final String ROLE = "MANAGER";

    public Manager() {
    }

    public Manager(String username, String password, List<Enterprise> enterprises) {
        this.username = username;
        this.password = password;
        this.enterprises = enterprises;
    }

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @ManyToMany
    @JoinTable(
            name = "manager_enterprise",
            joinColumns = @JoinColumn(name = "manager_id"),
            inverseJoinColumns = @JoinColumn(name = "enterprise_id")
    )
    @Nullable
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("enterpriseIds")
    private List<Enterprise> enterprises;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(ROLE));
        return authorities;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Nullable
    public List<Enterprise> getEnterprises() {
        return enterprises;
    }

    public void setEnterprises(@Nullable List<Enterprise> enterprises) {
        this.enterprises = enterprises;
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
        return true;
    }
}
