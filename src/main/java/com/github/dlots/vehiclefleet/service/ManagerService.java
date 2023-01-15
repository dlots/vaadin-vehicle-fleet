package com.github.dlots.vehiclefleet.service;

import com.github.dlots.vehiclefleet.data.entity.Manager;
import com.github.dlots.vehiclefleet.data.repository.ManagerRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

@Component
public class ManagerService implements UserDetailsService {
    private static final String LOGOUT_SUCCESS_URL = "/";

    private final ManagerRepository managerRepository;

    public ManagerService(ManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Manager manager = managerRepository.findByUsername(username);
        if (manager == null) {
            throw new UsernameNotFoundException(username);
        }
        return manager;
    }

    public UserDetails getAuthenticatedManager() {
        SecurityContext context = SecurityContextHolder.getContext();
        Object principal = context.getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return (UserDetails) context.getAuthentication().getPrincipal();
        }
        // Anonymous or no authentication.
        return null;
    }

    public void logout() {
        UI.getCurrent().getPage().setLocation(LOGOUT_SUCCESS_URL);
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(
                VaadinServletRequest.getCurrent().getHttpServletRequest(), null,
                null);
    }
}