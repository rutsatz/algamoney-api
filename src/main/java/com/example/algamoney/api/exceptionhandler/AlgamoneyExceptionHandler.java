package com.example.algamoney.api.exceptionhandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

		List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));

		// Peço para o Spring tratar a exceção. Só que eu consigo passar um body.
		return handleExceptionInternal(ex, erros, headers, HttpStatus.BAD_REQUEST, request);
	}

	/**
	 * Trata as mensagens quando o argumentos de um método ou request não são
	 * válidos. Para ele validar, é necessário estar anotado com o @Valid.
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		List<Erro> erros = criarListaDeErros(ex.getBindingResult());
		return handleExceptionInternal(ex, erros, headers, HttpStatus.BAD_REQUEST, request);
	}

	/**
	 * Trata para os casos de campos inválidos, pois a validação pode ter falhado em
	 * vários campos.
	 *
	 * @return
	 */
	private List<Erro> criarListaDeErros(BindingResult bindingResult) {
		List<Erro> erros = new ArrayList<>();

		// Percorre todos os atributos que foram validados na entidade.
		for (FieldError fieldError : bindingResult.getFieldErrors()) {
			// Usa o messageSource passando direto o fieldError, pois lá no arquivo de
			// properties tratamos a mensagem para cada campo da entidade, e assim ele
			// consegue recuperar as mensagens pelo código do @NotNull.
			String mensagemUsuario = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
			String mensagemDesenvolvedor = fieldError.toString();
			erros.add(new Erro(mensagemUsuario, mensagemDesenvolvedor));
		}
		return erros;
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
