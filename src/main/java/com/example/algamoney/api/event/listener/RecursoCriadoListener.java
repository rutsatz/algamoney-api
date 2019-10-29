package com.example.algamoney.api.event.listener;

import java.net.URI;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.algamoney.api.event.RecursoCriadoEvent;

@Component
public class RecursoCriadoListener implements ApplicationListener<RecursoCriadoEvent> {

	@Override
	public void onApplicationEvent(RecursoCriadoEvent recursoCriadoEvent) {
		HttpServletResponse response = recursoCriadoEvent.getResponse();
		Long codigo = recursoCriadoEvent.getCodigo();

		adicionarHeaderLocation(response, codigo);
	}

	private void adicionarHeaderLocation(HttpServletResponse response, Long codigo) {
		// No Rest, sempre que criamos um recurso, devemos retornar um header com o
		// location apontando para ele. Nesse caso, criamos usando o builder do Spring,
		// pegando a requisição atual e gerando uma nova uri, fazendo um append na url
		// atual e depois fazendo o replace pelo código. O Bulder retorna a nova URI.
		URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{codigo}").buildAndExpand(codigo).toUri();
		// Adiciona a nova URI no header.
		// Na nova versão do Spring, não é mais necessário adicionar manualmente, o
		// próprio Spring já adiciona.
		response.setHeader("Location", uri.toASCIIString());

	}

}
