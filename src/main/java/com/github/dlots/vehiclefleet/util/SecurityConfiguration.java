package com.github.dlots.vehiclefleet.util;

import com.github.dlots.vehiclefleet.rest.ApiController;
import com.github.dlots.vehiclefleet.views.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurity {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers(ApiController.API + "/**").authenticated()
                .and().httpBasic().and().sessionManagement().disable();

        super.configure(http);

        // This is important to register your login view to the
        // view access checker mechanism:
        setLoginView(http, LoginView.class);

        http.csrf().disable();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // Customize your WebSecurity configuration.
        super.configure(web);
    }

    @Bean(name = "pwdEncoder")
    public PasswordEncoder getPasswordEncoder() {
        DelegatingPasswordEncoder delPasswordEncoder = (DelegatingPasswordEncoder) PasswordEncoderFactories
                .createDelegatingPasswordEncoder();
        BCryptPasswordEncoder bcryptPasswordEncoder = new BCryptPasswordEncoder();
        delPasswordEncoder.setDefaultPasswordEncoderForMatches(bcryptPasswordEncoder);
        return delPasswordEncoder;
    }
}