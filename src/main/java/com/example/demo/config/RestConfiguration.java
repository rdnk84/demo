package com.example.demo.config;

import com.example.demo.model.auth.filters.CustomAuthenticationFilter;
import com.example.demo.model.auth.filters.CustomAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import static org.springframework.http.HttpMethod.POST;



//@Order(2)
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class RestConfiguration extends WebSecurityConfigurerAdapter {

    private static final String[] SWAGGER_ENDPOINT = {
            "/**swagger**/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/v2/api-docs",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    };

    //это те эндпоинты,на кот.можно ходить не смотря ни на что
    private static final String[] WHITELIST = {
            "/h2-console/**/**",
            "/docs/**",
            "/csrf/**",
            "/webjars/**"
    };

    @Qualifier("delegatedAuthenticationEntryPoint")
    private final AuthenticationEntryPoint authEntryPoint;

//    @Autowired
    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(encoder);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    //в этом методе я указываю какие типы запросов я могу слать и на какие endpoints
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManager());
        customAuthenticationFilter.setFilterProcessesUrl("/api/login");

        http
                .cors() //здесь указываю, что все запросы фильтруются by CORSFilter
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()//те на ЛЮБЫЕ endpoints можно слать запроты типа OPTIONS
                .antMatchers(HttpMethod.GET, "/users/**").permitAll()//все GET запросы с эндпоинтом "/users" - те гетзапросы UserController  в данном приложении
                .antMatchers("/api/login/**", "/api/token/refresh/**").permitAll()
                .antMatchers(SWAGGER_ENDPOINT).permitAll()
                .antMatchers(POST, "/users/**").hasAnyAuthority("ROLE_ADMIN")// запросы типа POST на эндпоинт "/users" (те UserController) могут слать только пользователи с ролью ADMIN
                .anyRequest().authenticated().and()//те только авторизованный пользователь может слать запросы (кроме тех,которые определены выше 113-117 строки)
                .exceptionHandling().authenticationEntryPoint(authEntryPoint)
                .and()
                .addFilter(customAuthenticationFilter)
                .addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    //здесь указываем некий "белый список" эндпоинтов, кот.будет игнорироваться для обработки
    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers(WHITELIST)
                .antMatchers();
    }

    //здесь мы наш CORSFilter инициализируем, а даьше конфигуратор подхватывает его в методе .cors() -в строке 108
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }
}