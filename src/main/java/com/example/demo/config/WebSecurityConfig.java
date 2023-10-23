package com.example.demo.config;

//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
//
//    //здесь определяем какой тип фильтра будет для авторизации
//    @Value("${auth.header}")
//    private String principalRequestHeader;
//
//    //здесь определяем значение для этого фильтра
//    @Value("${auth.token}")
//    private String principalRequestValue;
//
//    @Override
//    protected void configure(HttpSecurity httpSecurity) throws Exception {
//        AuthFilter filter = new AuthFilter(principalRequestHeader);
//        filter.setAuthenticationManager(authentication -> {
//            String principal = (String) authentication.getPrincipal();
//            if (!principalRequestValue.equals(principal)) {
//                throw new BadCredentialsException("The API key was not found or not the expected value.");
//            }
//            authentication.setAuthenticated(true);
//            return authentication;
//        });
//
////здесь же еще можно прикрепить jwt token, кот.цепляется при авторизации нового юзера
//        httpSecurity
//                .addFilter(filter)
//                .authorizeRequests()
//                .antMatchers("/users/**").permitAll()
//                .anyRequest()
//                .authenticated()
//                .and().csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//
//    }

//    private final UserDetailsService userDetailsService;
//    private final BCryptPasswordEncoder encoder;
//
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userDetailsService).passwordEncoder(encoder);
//    }
//
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManager());
//        customAuthenticationFilter.setFilterProcessesUrl("/api/login");
//        http.csrf().disable();
//        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//        http.authorizeRequests().antMatchers("/api/login/**", "/api/token/refresh/**").permitAll();
//        http.authorizeRequests().antMatchers(GET, "/api/user/**").hasAnyAuthority("ROLE_USER");
//        http.authorizeRequests().antMatchers(POST, "/api/user/save/**").hasAnyAuthority("ROLE_ADMIN");
//        http.authorizeRequests().anyRequest().authenticated();
//        http.addFilter(customAuthenticationFilter);
//        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
//    }
//
//    @Bean
//    @Override
//    public AuthenticationManager authenticationManager() throws Exception {
//        return super.authenticationManager();
//    }
//}
