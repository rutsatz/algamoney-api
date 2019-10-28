package com.example.algamoney.api.resource;

import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.algamoney.api.model.Categoria;
import com.example.algamoney.api.repository.CategoriaRepository;

@RestController
@RequestMapping("/categorias")
public class CategoriaResource {

	@Autowired
	private CategoriaRepository categoriaRepository;

	@GetMapping
	public List<Categoria> listar() {
		return categoriaRepository.findAll();
	}

	@PostMapping
//	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Categoria> criar(@RequestBody Categoria categoria, HttpServletResponse response) {
		Categoria categoriaSalva = categoriaRepository.save(categoria);
		// No Rest, sempre que criamos um recurso, devemos retornar um header com o
		// location apontando para ele. Nesse caso, criamos usando o builder do Spring,
		// pegando a requisição atual e gerando uma nova uri, fazendo um append na url
		// atual e depois fazendo o replace pelo código. O Bulder retorna a nova URI.
		URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{codigo}")
				.buildAndExpand(categoriaSalva.getCodigo()).toUri();
		// Adiciona a nova URI no header.
		response.setHeader("Location", uri.toASCIIString());

		return ResponseEntity.created(uri).body(categoriaSalva);
	}

	@GetMapping("/{codigo}")
	public ResponseEntity<?> buscarPeloCodigo(@PathVariable Long codigo) {
		return this.categoriaRepository.findById(codigo).map(categoria -> ResponseEntity.ok(categoria))
				.orElse(ResponseEntity.notFound().build());
	}

}
