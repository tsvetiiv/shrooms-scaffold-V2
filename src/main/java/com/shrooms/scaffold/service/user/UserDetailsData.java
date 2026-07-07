package com.shrooms.scaffold.service.user;


import com.shrooms.scaffold.model.entity.user.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class UserDetailsData implements UserDetails {

    private UUID id;
    private String username;
    private String password;
    private RoleType roleType;
    private boolean active;
    private boolean blocked;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {


        return List.of(new SimpleGrantedAuthority("ROLE_" + roleType.name()));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isEnabled() {
        return this.active;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.blocked;
    }
}
