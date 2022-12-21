package com.example.cloudstorage.service;

import com.example.cloudstorage.entities.AuthorizationData;
import com.example.cloudstorage.repositories.AuthorizationDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationDataService implements UserDetailsService {
    @Autowired
    AuthorizationDataRepository repository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        AuthorizationData data = repository
                .findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        return AuthorizationDataDetails.build(data);
    }
}
