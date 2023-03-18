package com.inventory.mgt.sytem.inventory.mgt.system.security;

import com.inventory.mgt.sytem.inventory.mgt.system.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Data
public class UserPrincipal implements UserDetails {

    private  Long id;
    private  String username;
    private  String lastName;
    private  String firstName;
    private String password;
    private String email;
    private  String address;
    private  String userPhoneNo;
    private Character gender;
    private Collection<? extends  GrantedAuthority> authorities;

    public UserPrincipal(Long id, String username,
                         String lastName,
                         String firstName,
                         String password,
                         String email,
                         String address,
                         String userPhoneNo,
                         Character gender,
                         Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.lastName = lastName;
        this.firstName = firstName;
        this.password = password;
        this.email = email;
        this.address = address;
        this.userPhoneNo = userPhoneNo;
        this.gender = gender;
        this.authorities = authorities;
    }

    // Create a static method that returns currently logged-in user
    public static UserPrincipal create(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream().map(role ->
                new SimpleGrantedAuthority(role.getRoleName().name())

        ).collect(Collectors.toList());
        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getLastName(),
                user.getFirstName(),
                user.getPassword(),
                user.getEmail(),
                user.getUserAddress(),
                user.getPhoneNo(),
                user.getGender(),
                authorities
        );
    }


    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }
    public  String getLastName(){
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
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
