package br.com.erudio.service;

import br.com.erudio.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository repository;

    private final Logger logger = Logger.getLogger(UserService.class.getName());

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Finding one user by name " + username);
        var user = repository.findByUsername(username);

        if (user != null) {
            return user;
        }

        throw new UsernameNotFoundException("Username " + username + "not found!");
    }
}
