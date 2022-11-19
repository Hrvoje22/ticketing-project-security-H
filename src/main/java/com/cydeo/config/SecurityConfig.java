package com.cydeo.config;

import com.cydeo.service.SecurityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    private final SecurityService securityService;
    private final AuthSuccessHandler authSuccessHandler;

    public SecurityConfig(SecurityService securityService, AuthSuccessHandler authSuccessHandler) {
        this.securityService = securityService;
        this.authSuccessHandler = authSuccessHandler;
    }

    //    @Bean
//    public UserDetailsService userDetailsService(PasswordEncoder encoder){
//
//        //Spring users, not entity - ours
//
//        List<UserDetails> userList = new ArrayList<>();
//        userList.add(
//                new User("mike",encoder.encode("password"), Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")))
//                );
//
//        userList.add(
//                new User("ozzy",encoder.encode("password"), Arrays.asList(new SimpleGrantedAuthority("ROLE_MANAGER")))
//                );
//
//        return new InMemoryUserDetailsManager(userList);
//
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {


        return http
                .authorizeRequests()
//                .antMatchers(".user/**").hasRole("ADMIN")                //we don't need ROLE because its automatically concatenating
                .antMatchers("/user/**").hasAuthority("Admin")
                .antMatchers("/project/**").hasAuthority("Manager")
                .antMatchers("/task/employee/**").hasAuthority("Employee")
                .antMatchers("/task/**").hasAuthority("Manager")
//                .antMatchers("/task/**").hasAnyRole("EMPLOYEE","ADMIN")
//                .antMatchers("/task/**").hasAnyAuthority("ROLE_EMPLOYEE") //needs to match with Simple Granted Authority
                .antMatchers(
                        "/",
                        "/login",
                        "/fragments/**",
                        "/assets/**",
                        "/images/**"

                ).permitAll()
                .anyRequest().authenticated()
                .and()
                //.httpBasic() //pop up box
                .formLogin()
                    .loginPage("/login")
                    //.defaultSuccessUrl("/welcome")
                    .successHandler(authSuccessHandler) //customize
                    .failureUrl("/login?error=true")
                    .permitAll()
                .and()
                .logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/login")
                .and()
                .rememberMe()
                    .tokenValiditySeconds(120)
                    .key("cydeo")
                    .userDetailsService(securityService) //capture the user
                .and()
                .build();
    }



}
