package me.molchanoff.microblog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

/**
 * This class is used for Spring Security configuration
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin().loginPage("/login").and().rememberMe().tokenValiditySeconds(86400)
                .key("microblog").and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/")
                .and().authorizeRequests().antMatchers("/myposts").authenticated()
                .and().authorizeRequests().antMatchers("/feed").authenticated()
                .and().authorizeRequests().antMatchers("/profile").authenticated()
                .and().authorizeRequests().antMatchers("/post/new").authenticated()
                .and().authorizeRequests().antMatchers("/post/vote").authenticated()
                .and().authorizeRequests().antMatchers("/post/unvote").authenticated()
                .and().authorizeRequests().antMatchers("/user/follow").authenticated()
                .and().authorizeRequests().antMatchers("/user/unfollow").authenticated().anyRequest().permitAll()
                .and().authorizeRequests().antMatchers("/admin").hasRole("ADMIN");

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().dataSource(dataSource)
                .usersByUsernameQuery("select username, password, enabled from Users where username = ?")
                .authoritiesByUsernameQuery("select  u.username, a.role from Users u join Authorities a on u.id = a.user_id where u.username = ?");
    }
}
