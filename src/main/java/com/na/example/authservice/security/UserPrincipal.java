package com.na.example.authservice.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserPrincipal implements UserDetails {

    private String username;
    @JsonIgnore
    private String password;
    private String email;
    private String organization;
    private List<Role> roles;
    private List<Integer> accounts;


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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        var authorities = this.roles.stream().map(authority -> new SimpleGrantedAuthority(authority.name())).collect(Collectors.toList());
        authorities.addAll(this.accounts.stream().map(authority -> new SimpleGrantedAuthority(authority.toString())).collect(Collectors.toList()));
        authorities.add(new SimpleGrantedAuthority(organization));
        return authorities;
    }
}
