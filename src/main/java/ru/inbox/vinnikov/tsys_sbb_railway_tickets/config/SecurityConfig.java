package ru.inbox.vinnikov.tsys_sbb_railway_tickets.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.service.CustomUserDetailsService;

@EnableWebSecurity//(debug = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private CustomUserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override // метод для настроек безопасности
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/sbb/v1/admin/**").access("hasRole('ADMIN')")
//                .antMatchers("/sbb/v1/user/**").access("hasRole('USER')")
                .antMatchers("/sbb/v1/user/**").access("hasAnyRole('USER', 'ADMIN')")
                .antMatchers("/**").permitAll()
// три строки выше - это какие запросы каким ролям доступны
    // а строки ниже - это всё что связано со входом и выходом
                .and().formLogin().permitAll()  //login configuration - форма входа доступна всем
                .loginPage("/sbb/v1/login_de")
                //.loginProcessingUrl("/sbb/v1/login-handler_de")
                .loginPage("/sbb/v1/login_ru") // форма входа доступна по ссылке /login
                //.loginProcessingUrl("/sbb/v1/login-handler_ru")
                .loginPage("/sbb/v1/login_uk")
                //.loginProcessingUrl("/sbb/v1/login-handler_uk")
                .loginProcessingUrl("/sbb/v1/login-handler")
                .usernameParameter("login")
                .passwordParameter("password")
                // если пользователь авторизовался успешно, то будет перенаправлен на свой аккаунт
                //.defaultSuccessUrl("/sbb/v1/admin/account"/*,"hasRole('ADMIN')"*/)
                //.defaultSuccessUrl("/sbb/v1/user/account"/*,"hasRole('USER')"*/)
                .defaultSuccessUrl("/sbb/v1/login-successful")
                .failureForwardUrl("/sbb/v1/login-failure")
                .and().logout()// настройки для выхода
                .logoutRequestMatcher(new AntPathRequestMatcher("/sbb/v1/logout")) // гет запрос на логаут
                .logoutSuccessUrl("/sbb/v1/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .and()
                .exceptionHandling()
                .accessDeniedPage("/sbb/v1/error");
    }

    @Override // метод для регистрации сервиса или провайдера
    public void configure(AuthenticationManagerBuilder auth) throws Exception
    { //
      auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
//System.out.println("--------99999 " + auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder()));
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    { // нужен если в нашей таблице БД пароли хранятся в зашифрованном виде - так и должно быть
        BCryptPasswordEncoder b = new BCryptPasswordEncoder();
        //System.out.println("******------8888 Encoder:" + b);
        return b;
    }
}