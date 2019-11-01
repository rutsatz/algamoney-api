package com.example.algamoney.api.token;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * O Spring não tem algo pronto para colocar o refresh token como cookie http.
 * Então implementamos a interface ResponseBodyAdvice, que captura todas as
 * respostas antes de serem enviadas de volta para o cliente e tiramos o refresh
 * token da requisição e colocamos no cookie.
 *
 * @author rafael.rutsatz
 *
 */
@ControllerAdvice
public class RefreshTokenPostProcessor implements ResponseBodyAdvice<OAuth2AccessToken> {

	/**
	 * Esse método é um filtro, que é executado para verificar se deve executar o
	 * método abaixo (beforeBodyWrite).
	 */
	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		// Somente deve executar quando o método for o postAccessToken.
		return returnType.getMethod().getName().equals("postAccessToken");
	}

	/**
	 * Aplico a regra nesse método. No body, eu consigo pegar o valor que seria
	 * enviado para o cliente.
	 */
	@Override
	public OAuth2AccessToken beforeBodyWrite(OAuth2AccessToken body, MethodParameter returnType,
			MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
			ServerHttpRequest request, ServerHttpResponse response) {

		// Para adicionar o refresh token no cookie http, eu preciso do
		// HttpServletRequest e HttpServletResponse. Como eu recebo ServerHttpRequest e
		// ServerHttpResponse, preciso convertê-los.
		HttpServletRequest req = ((ServletServerHttpRequest) request).getServletRequest();
		HttpServletResponse resp = ((ServletServerHttpResponse) response).getServletResponse();

		// Para conseguir remover o refreshToken do body, eu preciso fazer um cast para
		// DefaultOAuth2AccessToken, pois se não não tenho o método set para poder setar
		// para null.
		DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) body;

		// Recupera o refresh token.
		String refreshToken = body.getRefreshToken().getValue();

		adicionarRefreshTokenNoCookie(refreshToken, req, resp);
		removerRefreshTokenDoBody(token);

		return body;
	}

	private void removerRefreshTokenDoBody(DefaultOAuth2AccessToken token) {
		token.setRefreshToken(null);
	}

	private void adicionarRefreshTokenNoCookie(String refreshToken, HttpServletRequest req, HttpServletResponse resp) {
		// Crio um cookie normal.
		Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
		// Somente acessível por http. Não será possível recuperá-lo por javascript.
		refreshTokenCookie.setHttpOnly(true);
		// Indica se o cookie deve ser enviado somente quando for seguro, com HTTPS.
		refreshTokenCookie.setSecure(false); // TODO: Mudar para true em produção.
		// Para qual url o cookie deve ser enviado? Para isso uso o request. Então se
		// tiver algum contextPath, ele vai adicionar.
		refreshTokenCookie.setPath(req.getContextPath() + "oauth/token");
		// Defino em quanto tempo esse cookie vai expirar, em dias. (2592000 = 30 dias)
		refreshTokenCookie.setMaxAge(2592000);
		// Adiciono o cookie na resposta.
		resp.addCookie(refreshTokenCookie);
	}

}
