package com.share.fileupload.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;
import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.share.fileupload.entity.RoleEntity;
import com.share.fileupload.entity.UserEntity;
import com.share.fileupload.repository.RoleRepository;
import com.share.fileupload.repository.UserRepository;

@Service("userService")
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private UserDetails buildUserForAuthentication(UserEntity user, List<GrantedAuthority> authorities) {
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
                user.isActive(), true, true, true, authorities);
    }

    @Override
    public UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserEntity findUserById(int id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(userName);
        var authorities = getUserAuthority(user.getRoles());

        return buildUserForAuthentication(user, authorities);
    }

    @Override
    public void saveUser(UserEntity user) {
        bCryptPasswordEncoder = new BCryptPasswordEncoder();
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActive(false);

        var userRole = roleRepository.findByRole("USER");
        
        if (userRole == null) {
            throw new ValidationException();
        } 

        user.setRoles(new HashSet<RoleEntity>(Arrays.asList(userRole)));
        userRepository.save(user);
    }

    public void turnStatusUser(int id) {
        var user = findUserById(id);

        if (user != null) {
            boolean status = user.isActive();

            status = !status;
            user.setActive(status);
            updateUser(user);
        }
    }

    @Override
    public void updateUser(UserEntity user) {
        userRepository.save(user);
    }

    public List<UserEntity> getAllUsers() {
        var users = userRepository.findAll();

        if (users == null) {
            users = new ArrayList<>();
        }

        return users;
    }

    public UserEntity getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = null;

        if (auth != null) {
            user = findUserByEmail(auth.getName());

            if ((user != null) && user.isActive()) {
                return user;
            }
        }

        user = new UserEntity();
        user.setId(0);

        return user;
    }

    public boolean isLoginCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            var user = findUserByEmail(auth.getName());

            if ((user != null) && user.isActive()) {
                return true;
            }
        }

        return false;
    }

    private List<GrantedAuthority> getUserAuthority(Set<RoleEntity> userRoles) {
        var roles = new HashSet<GrantedAuthority>();

        for (RoleEntity role : userRoles) {
            roles.add(new SimpleGrantedAuthority(role.getRole()));
        }

        var grantedAuthorities = new ArrayList<GrantedAuthority>(roles);

        return grantedAuthorities;
    }
}
