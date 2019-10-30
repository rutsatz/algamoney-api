package com.example.algamoney.api.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.example.algamoney.api.model.Pessoa;
import com.example.algamoney.api.repository.PessoaRepository;

@Service
public class PessoaService {

	@Autowired
	private PessoaRepository pessoaRepository;

	public Pessoa atualizar(Long codigo, Pessoa pessoa) {
		Pessoa pessoaSalva = this.pessoaRepository.findById(codigo)
				// Se não encontrou row, lança a exceção, para manter o tratamento e retornar o
				// Http 404.
				.orElseThrow(() -> new EmptyResultDataAccessException(1));

		// Copia as propriedades de um objeto para outro. Eu passo a origem, o destino e
		// uma lista de propriedades para ignorar. Então copio os dados do objeto
		// pessoa, que estou recebendo do usuário, com os dados que devem ser
		// atualizados e copio para o objeto que veio do banco de dados, exceto o
		// codigo.
		BeanUtils.copyProperties(pessoa, pessoaSalva, "codigo");

		return this.pessoaRepository.save(pessoaSalva);
	}

}
