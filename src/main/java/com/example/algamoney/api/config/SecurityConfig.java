package com.example.algamoney.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	/**
	 * Configura as permissões de usuário, validao de senha, criptografia.
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser("admin").password("{noop}admin").roles("ROLE");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				// Libero todas as requisições para /categorias, sem autenticar.
				.antMatchers("/categorias").permitAll()
				// Digo que para qualquer requisição, eu preciso estar autenticado.
				.anyRequest().authenticated()
				//
				.and()
				// Defino o tipo de autenticação que vou usar.
				.httpBasic()
				// Desabilita a criação de sessão, a api não terá sessão.
				.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				// Desabilita o CSRF. Não vai precisar na Api.
				.and().csrf().disable();
	}

}
