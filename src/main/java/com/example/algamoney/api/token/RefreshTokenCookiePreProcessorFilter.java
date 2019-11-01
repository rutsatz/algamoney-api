package com.example.algamoney.api.token;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.catalina.util.ParameterMap;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Classe que vai interceptar a requisição enviada pelo cliente. Como o refresh
 * token está sendo enviado como cookie, o browser sempre vai mandar o cookie na
 * requisição. Colocamos o @Order para definir que isso deve ser feito primeiro
 * que todo mundo.
 * 
 * @author rafael.rutsatz
 *
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RefreshTokenCookiePreProcessorFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;

		// Verifico se a requisição é para /oauth/token e se é uma requisição de
		// refresh_token e também se tenho os cookies.
		if ("/oauth/token".equalsIgnoreCase(req.getRequestURI())
				&& "refresh_token".equals(req.getParameter("grant_type")) && req.getCookies() != null) {

			// Faço um for nos cookies para pegá-los.
			String refreshToken = Stream.of(req.getCookies())
					// Filtro pelo meu cookie de refreshToken
					.filter(cookie -> "refreshToken".equals(cookie.getName()))
					// Pego o refresh cookie da lista.
					.findFirst()
					// Recupero o valor do refresh cookie.
					.map(cookie -> cookie.getValue()).orElse(null);

			req = new MyServletRequestWrapper(req, refreshToken);
		}

		chain.doFilter(req, response);
	}

	/**
	 * Crio uma classe auxiliar para adicionar os parâmetros na requisição. Pois
	 * depois que a requisição está pronta, eu não consigo adicionar parâmetros
	 * nela. Então me utilizo dessa técnica. Ai eu substituo a requisição atual por
	 * essa classe aqui, pois ela também recebe a requisição.
	 * 
	 * @author rafael.rutsatz
	 *
	 */
	static class MyServletRequestWrapper extends HttpServletRequestWrapper {

		private String refreshToken;

		public MyServletRequestWrapper(HttpServletRequest request, String refreshToken) {
			super(request);
			this.refreshToken = refreshToken;
		}

		/**
		 * Ai eu sobrescrevo o getParameterMap, para ao invés de usar o da requisição,
		 * ele vai usar o nosso.
		 */
		@Override
		public Map<String, String[]> getParameterMap() {
			// Já inicio o map com os parâmetros da requisição original.
			ParameterMap<String, String[]> map = new ParameterMap<>(getRequest().getParameterMap());
			// Esse valor refresh_token é o que o Spring Security vai usar para recuperar o
			// refresh token
			map.put("refresh_token", new String[] { refreshToken });
			// Bloqueia o map contra alterações. Esse é o certo, o map da requisição ficar
			// travado.
			map.setLocked(true);
			return map;
		}

	}

}
