package com.example.algamoney.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Precisaremos criar uma nova classe de configuração para evitarmos problemas
 * com algumas das dependências que usamos acima.
 *
 * Sem essa classe, não será possível injetar a propriedade
 * AuthenticationManager que está como dependência em AuthorizationServerConfig.
 * Por isso precisamos definir como um Bean em nossa classe de configuração.
 *
 * @author raffa
 *
 */
@Configuration
@EnableWebSecurity
public class OAuthSecurityConfig extends WebSecurityConfigurerAdapter {

	@Bean
	@Override
	public AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}

	/**
	 * Também teremos problema com o PasswordEncoder, que precisamos definir nessa
	 * nova versão.
	 *
	 * PS.: NoOpPasswordEncoder está como deprecated por não ser recomendado sua
	 * utilização (pois não realiza nenhum tipo de encriptação), porém para manter a
	 * compatibilidade com a aula, iremos usá-lo. Em aulas posteriores vamos alterar
	 * essa parte para algo que possa ser usado em produção.
	 *
	 * @return
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
