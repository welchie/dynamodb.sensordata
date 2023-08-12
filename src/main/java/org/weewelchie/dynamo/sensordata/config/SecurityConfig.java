package org.weewelchie.dynamo.sensordata.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.weewelchie.dynamo.sensordata.authentication.AuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf()
                .disable().
                authorizeRequests()
                .requestMatchers(AntPathRequestMatcher.antMatcher("home/**")).permitAll()
                .and()
                .authorizeRequests().
                requestMatchers(AntPathRequestMatcher.antMatcher("sensordata/**")).denyAll().anyRequest().authenticated()
                .and()
                .addFilterBefore(new AuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }


//    public void configure(WebSecurity web) throws Exception {
//        web.ignoring().requestMatchers(AntPathRequestMatcher.antMatcher("/home/**"));
//    }
}
