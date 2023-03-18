 package com.inventory.mgt.sytem.inventory.mgt.system.security;

import com.inventory.mgt.sytem.inventory.mgt.system.exception.AppException;
import com.inventory.mgt.sytem.inventory.mgt.system.exception.ResourceNotFoundException;
import com.inventory.mgt.sytem.inventory.mgt.system.model.User;
import com.inventory.mgt.sytem.inventory.mgt.system.repository.LoginHistoryRepo;
import com.inventory.mgt.sytem.inventory.mgt.system.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

 @Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    LoginHistoryRepo loginHistoryRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findUserByUsername(username);

        if (!optionalUser.isPresent()) {
            throw new UsernameNotFoundException("Username not found " + username);
        } else {
            User user = optionalUser.get();
              // check if user is activated to log in
            if (user.isActive() == Boolean.TRUE) {
                return UserPrincipal.create(user);
            }
            else {
                log.warn("User is disabled!");
                throw new AppException("User is disable");
            }


        }
    }

    public UserDetails loadUserByUserId(Long userId) {
        User user = userRepository.
                findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return UserPrincipal.create(user);
    }
}










