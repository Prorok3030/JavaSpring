package com.example.tutorboot.config;

import com.example.tutorboot.models.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers("/", "/registration", "/about").permitAll()
                    //.antMatchers("/taskEdit/**").hasRole(Role.USER.name()) (возможно работает только со способом через Bean (ниже)
                    .anyRequest().authenticated()
                .and()
                    .formLogin() //даёт формочку с авторизацией (если нет своей, то ставит из коробки)
                    .loginPage("/login")
                    .permitAll()
                .and()
                    .logout()
                    .logoutSuccessUrl("/")
                    .permitAll();
                     http.csrf().disable(); //отключение csrf (хз, мб не из-за этого не работало)
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(NoOpPasswordEncoder.getInstance())
                .usersByUsernameQuery("select username, password, active from users where username=?")
                .authoritiesByUsernameQuery("select u.username, ur.roles from users u inner join user_role ur on ur.user_id where u.username=?");
    }





    /*@Bean
    @Override
    public UserDetailsService userDetailsService() {
        UserDetails admin =
                User.withDefaultPasswordEncoder()
                        .username("Prorok")
                        .password("123")
                        .roles(Role.ADMIN.name())
                        .build();
        UserDetails user =
                User.withDefaultPasswordEncoder()
                        .username("Chel")
                        .password("123")
                        .roles(Role.USER.name())
                        .build();

        return new InMemoryUserDetailsManager(user, admin);
    }*/
}