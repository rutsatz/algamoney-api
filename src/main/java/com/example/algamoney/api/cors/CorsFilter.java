package com.example.algamoney.api.cors;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Implementa o filtro de Cors. Foi feito assim pois o Spring OAuth ainda não
 * possui uma integração para configurar o CORS. Se usar o CORS do Spring,
 * quando o browser mandar a requisição OPTIONS para verificar se pode mandar o
 * Request com essa origem, o Security vai barrar, vou vai pedir autenticação.
 * Mas como é o browser que implementa isso, eu não consigo controlar, pois
 * mesmo que eu mande o request de autenticação, ele vai mandar o OPTIONS
 * primeiro. Ai a solução encontrada foi tratar através de um filtro.
 * 
 * O Cors não é nada de mais, são somente os cabeçalhos HTTP, que começam com
 * Access-Control.
 *
 * @author rafael.rutsatz
 *
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

	/**
	 * Para testes vai ser uma origem, para produção vai ser outra.
	 */
	private String originPermitida = "http://localhost:8000"; // TODO: Configurar para diferentes ambientes

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		// Esses headers eu adiciono sempre, para continuar funcionando.
		response.setHeader("Access-Control-Allow-Origin", originPermitida);
		// Esse daqui eu adiciono por causa do Cookie do refresh token, para que esse
		// cookie seja enviado.
		response.setHeader("Access-Control-Allow-Credentials", "true");

		// Então, se a requisição for um OPTIONS e a origem que veio do browser for da
		// nossa origem permitida, eu vou autorizar o pre_flight request. Caso
		// contrário, o Cors não vai estar habilitado e não vai funcionar.
		if ("OPTIONS".equals(request.getMethod()) && originPermitida.equals(request.getHeader("Origin"))) {
			// No caso do OPTIONS, eu não posso deixar continuar, pois se eu continuar, ou
			// seja, fizer o chain.doFilter, ele vai chegar no filtro do Spring Security e o
			// Spring Security vai bloquear a requisição, ou seja, o Spring vai retornar
			// 403, que é acesso proibido, dizendo que a requisição com essa origem não pode
			// ser enviada.
			// Então eu retorno 200, OK, para o browser mandar a próxima requisição. E eu
			// também seto os headers do CORS, simulando como se o cors tivesse aprovado.
			// Passamos os métodos que serão permitidos. Qualquer método diferente desse
			// será bloqueado.
			response.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, PUT, OPTIONS");
			// Os headers que vamos permitir. Qualquer requisição com header diferente, será
			// barrada.
			response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept");
			// O tempo que o browser vai esperar até a próxima requisição, em segundos.
			// Nesse caso, o browser vai requisitar a cada uma hora.
			response.setHeader("Access-Control-Max-Age", "3600");

			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			// Se não for, ai continua o processamento normal.
			chain.doFilter(req, resp);
		}
	}

}
