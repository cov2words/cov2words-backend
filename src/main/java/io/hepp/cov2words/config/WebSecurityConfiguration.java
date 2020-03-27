package io.hepp.cov2words.config;

import io.hepp.cov2words.common.constant.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * For our RestController it is always advised to use a custom web security configuration,
 * that fulfills the following:
 * - deactivate csrf checks
 * - any request needs an authorization by role! (otherwise this can get buggy for our RestControllers)
 * <p>
 *
 * @author Thomas Hepp, thomas@hepp.io
 */
@Configuration
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final String[] authRoles;

    @Autowired
    public WebSecurityConfiguration(@Value("${spring.security.user.roles}") String[] authRoles) {
        this.authRoles = authRoles;
    }

    /**
     * Secure the endpoints with HTTP Basic authentication and disable csrf.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //HTTP Basic authentication
                .httpBasic()
                .and()
                .authorizeRequests().antMatchers("/actuator/**").hasAnyRole(authRoles)
                .and()
                .authorizeRequests().antMatchers(Paths.BASE_PATH + "/**").hasAnyRole(authRoles)
                .and()
                .authorizeRequests().anyRequest().permitAll()
                .and()
                .csrf().disable();
    }
}
