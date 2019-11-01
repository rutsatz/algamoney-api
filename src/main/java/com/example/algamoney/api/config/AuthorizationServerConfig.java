package com.example.algamoney.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * Configura o servidor de autorização.
 *
 * @author raffa
 *
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	/**
	 * Injeta a classe que vai gerenciar as autenticações. Ele que vai receber o
	 * usuário e senha e verificar se são válidos e retornar um token.
	 */
	@Autowired
	private AuthenticationManager authenticationManager;

	/**
	 * Configura a aplicação, ou seja, o cliente. No exemplo do facebook, seria o
	 * site terceiro, que solicita o token.
	 */
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		// Como temos somente um cliente, que é a nossa aplicação angular, vamos deixar
		// fixo em memória. Mas se tivessemos mais clientes terceiros, que quissesem
		// consumir a api, poderiamos usar o banco de dados também, passando uma conexão
		// jdbc.
		clients.inMemory()
				// Passo o da aplicação terceira. Como vamos ter somente o angular, chamamos
				// nosso front de angular mesmo. Assim, passamos angular como id do cliente.
				.withClient("angular")
				// Passo a senha que o cliente precisa usar para acessar o servidor de
				// autorizações. Ou seja, para o cliente ter acesso, ele precisa mandar um POST
				// para /oauth/token com esse usuário e senha. E no corpo da requisição, ele
				// passa o nome do cliente, passa o usuário e senha que são do usuário mesmo,
				// por exemplo, admin/admin. Passa também o grant_type, que é password. Se
				// estiver correto, ele retorna o token, que o cliente pode usar para acessar a
				// api.
				.secret("@ngul@r0")
				// E também passo uma lista de escopos desse cliente. Com isso, eu consigo
				// limitar o acesso desse cliente. Dessa forma, posso definir escopos diferentes
				// para clientes diferentes. Essa strings, sou eu que defino. Depois eu uso elas
				// no acesso aos métodos.
				.scopes("read", "write")
				// Defino o tipo de fluxo oAuth que vou usar (password flow).Defino o Grant Type
				// como password flow. Faço isso quando a aplicação cliente
				// é de inteira confiança, pois ela vai ter acesso a senha do usuário.
				// Também adiciona o fluxo de refresh token. Dessa forma, quando um token
				// expira, ao invés de fazer um request novo, mandando o usuário e senha, é
				// solicitado um refresh token. E esse token do refresh é salvo num cookie
				// https, que o próprio browser vai mandar, ou seja, esse token não vai ser
				// acessível por javascript. Abaixo, também é configurado o tempo de validade
				// desse refresh token.
				.authorizedGrantTypes("password", "refresh_token")
				// Defino quantos segundos esse token vai ficar ativo. (1800 s = 30 min). Então,
				// consigo usar o mesmo token por 30 minutos.
				.accessTokenValiditySeconds(20)
				// Configura a validade do refresh token para durar 1 dia.
				.refreshTokenValiditySeconds(3600 * 24);
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints
				// Eu preciso armazenar esses tokens em algum lugar. Onde? Num TokenStore.
				.tokenStore(tokenStore())
				// Como mudamos do token oAuth para o JWT, precisamos adicionar um token
				// converter.
				.accessTokenConverter(accessTokenConverter())
				// Setando para não reusar o refresh token, sempre que eu pedir um novo access
				// token usando o refresh token, um novo refresh token é gerado. Então, enquanto
				// o usuário estiver usando a aplicação, ele não vai ser deslogado, pois o
				// refresh token não vai expirar e assim vamos conseguir ficar buscando novos
				// access tokens. Se não setarmos isso, o refresh token vai ter o tempo de 24h e
				// depois disse vai expirar e o usuário vai precisar logar novamente.
				.reuseRefreshTokens(false)
				// Passa o manager dos tokens, para ele poder validar os tokens recebidos.
				.authenticationManager(authenticationManager);
	}

	/**
	 * Faz a conversão entre tokens JWT e tokens oAuth. Quem precisar de um token
	 * converter, consegue recuperar através deste bean.
	 *
	 * @return
	 */
	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
		// Seta a chave que valida o token. É o campo secret do token.
		accessTokenConverter.setSigningKey("algaworks");
		return accessTokenConverter;
	}

	@Bean
	public TokenStore tokenStore() {
		// Armazena os tokens em memória, por enquanto. Depois será usado o token JWT,
		// que não precisa ser armazenado assim.
//		return new InMemoryTokenStore();
		return new JwtTokenStore(accessTokenConverter());
	}

}
