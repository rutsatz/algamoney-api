package com.example.algamoney.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;

/**
 * Configura o servidor de recursos. Com o oAuth2, ele consulta o
 * AuthorizationServer para ver se um token recebido é válido.
 *
 * @author raffa
 *
 */
@Configuration
//@EnableWebSecurity
@EnableResourceServer
// Habilita o tratamento de segurança nos métodos. E também habilitamos as anotações Pre e Post.
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	/**
	 * Com o ResourceServer do oAuth2, o AuthenticationManagerBuilder é injetado pra
	 * gente. Por isso uso o Autowired.
	 */
//	@Autowired
//	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//		auth.inMemoryAuthentication().withUser("admin").password("admin").roles("ROLE");
//	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				// Libero todas as requisições para /categorias, sem autenticar.
				.antMatchers("/categorias").permitAll()
				// Digo que para qualquer requisição, eu preciso estar autenticado.
				.anyRequest().authenticated()
				// Defino o tipo de autenticação que vou usar.
//				.and().httpBasic()
				// Desabilita a criação de sessão, a api não terá sessão.
				.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				// Desabilita o CSRF. Não vai precisar na Api.
				.and().csrf().disable();
	}

	/**
	 * Configuro como stateless. Assim, somente é possível autenticação por token.
	 */
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.stateless(true);
	}

	/**
	 * Bean para fazer a segurança dos métodos (@EnableGlobalMethodSecurity)
	 * funcionar com o OAuth2.
	 *
	 * @return
	 */
	@Bean
	public MethodSecurityExpressionHandler createExpressionHandler() {
		return new OAuth2MethodSecurityExpressionHandler();
	}

}
