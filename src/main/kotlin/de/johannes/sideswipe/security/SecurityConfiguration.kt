package de.johannes.sideswipe.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.OrRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfiguration(private val provider: AuthenticationProvider): WebSecurityConfigurerAdapter() {
    private val protectedUrls: RequestMatcher = OrRequestMatcher(
        AntPathRequestMatcher("/api/v1/**")
    )

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(provider)
    }

    override fun configure(webSecurity: WebSecurity) {
        webSecurity.ignoring().antMatchers("/token/**")
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .exceptionHandling()
            .and()
            .authenticationProvider(provider)
            .addFilterBefore(authenticationFilter(), AnonymousAuthenticationFilter::class.java)
            .authorizeRequests()
            .requestMatchers(protectedUrls)
            .authenticated()
            .and()
            .headers()
            .httpStrictTransportSecurity()
            .disable()
            .and()
            .csrf().disable()
            .formLogin().disable()
            .httpBasic().disable()
            .logout().disable()
    }

    @Bean
    @Throws(java.lang.Exception::class)
    fun authenticationFilter(): AuthenticationFilter {
        val filter = AuthenticationFilter(protectedUrls)
        filter.setAuthenticationManager(authenticationManager())
        //filter.setAuthenticationSuccessHandler(successHandler());
        return filter
    }

    @Bean
    fun forbiddenEntryPoint(): AuthenticationEntryPoint {
        return HttpStatusEntryPoint(HttpStatus.FORBIDDEN)
    }
}
