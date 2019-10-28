package com.example.algamoney.api.exceptionhandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * ResponseEntityExceptionHandler captura exceções de resposta de entidades.
 * Adiciono o @ControllerAdvice, que faz com que a classe escute toda a
 * aplicação.
 * 
 * @author rafael.rutsatz
 *
 */
@ControllerAdvice
public class AlgamoneyExceptionHandler extends ResponseEntityExceptionHandler {

	/**
	 * Busca as mensagens lá do messages.properties.
	 */
	@Autowired
	private MessageSource messageSource;

	/**
	 * Captura as mensagens que o Spring não conseguiu ler.
	 */
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		// Recupera a mensagem do arquivo de properties. Passa o código da mensagem,
		// null pois não tem tenhum parâmetro adicional e o Locale corrente da
		// aplicação.
		String mensagemUsuario = messageSource.getMessage("mensagem.invalida", null, LocaleContextHolder.getLocale());
		// Adiciona uma mensagem para o desenvolvedor, que está consumindo a API.
		String mensagemDesenvolvedor = ex.getCause().toString();
		// Peço para o Spring tratar a exceção. Só que eu consigo passar um body.
		return handleExceptionInternal(ex, new Erro(mensagemUsuario, mensagemDesenvolvedor), headers,
				HttpStatus.BAD_REQUEST, request);
	}

	/**
	 * Representa o erro que é retornado. Como somente está sendo usada aqui, foi
	 * criado como uma classe interna mesmo.
	 * 
	 * @author rafael.rutsatz
	 *
	 */
	public static class Erro {

		private String mensagemUsuario;
		private String mensagemDesenvolvedor;

		public Erro(String mensagemUsuario, String mensagemDesenvolvedor) {
			this.mensagemUsuario = mensagemUsuario;
			this.mensagemDesenvolvedor = mensagemDesenvolvedor;
		}

		public String getMensagemUsuario() {
			return mensagemUsuario;
		}

		public String getMensagemDesenvolvedor() {
			return mensagemDesenvolvedor;
		}

	}

}
