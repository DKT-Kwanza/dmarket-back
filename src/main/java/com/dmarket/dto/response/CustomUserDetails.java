package com.dmarket.dto.response;

import com.dmarket.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
public class CustomUserDetails  implements UserDetails {

    private final User userEntity;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {

                return String.valueOf(userEntity.getUserRole());
            }
        });

        return collection;
    }

    @Override
    public String getPassword() {

        return userEntity.getUserPassword();
    }

    @Override
    public String getUsername() {

        return userEntity.getUserName();
    }

    public String getEmail(){
        return userEntity.getUserEmail();
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
