package com.example.algamoney.api.repository.lancamento;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.util.StringUtils;

import com.example.algamoney.api.model.Lancamento;
import com.example.algamoney.api.model.Lancamento_;
import com.example.algamoney.api.repository.filter.LancamentoFilter;

public class LancamentoRepositoryImpl implements LancamentoRepositoryQuery {

	@PersistenceContext
	private EntityManager manager;

	@Override
	public List<Lancamento> filtrar(LancamentoFilter lancamentoFilter) {
		// Criteria do JPA, pois a do Hibernate está depreciada.
		// Primeira coisa, pegar o builder de Criteria.
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		// Depois crio uma CriteriaQuery.
		CriteriaQuery<Lancamento> criteria = builder.createQuery(Lancamento.class);

		// ##### Inicio Filtros #####

		// Adicionar os filtros.
		// Primeiro crio um Root.
		Root<Lancamento> root = criteria.from(Lancamento.class);

		// criar as restrições
		Predicate[] predicates = criarRestricoes(lancamentoFilter, builder, root);
		criteria.where(predicates);

		// ##### Fim Filtros #####

		// Crio a Query.
		TypedQuery<Lancamento> query = manager.createQuery(criteria);
		return query.getResultList();
	}

	private Predicate[] criarRestricoes(LancamentoFilter lancamentoFilter, CriteriaBuilder builder,
			Root<Lancamento> root) {

		List<Predicate> predicates = new ArrayList<>();

		if (!StringUtils.isEmpty(lancamentoFilter.getDescricao())) {
			// where lower(descricao) like '%descricao%'
			// Para não precisar digitar uma string com o nome do atributo e correr o risco
			// de uma mudança na classe não ser feita ness string, é usado o
			// hibernate-jpamodelgen. Habilitamos ele na IDE e importamos o jar e ai ele
			// cria as classes com o _ no final, assim importamos e usamos essa classe ao
			// invés de digitar a string. Assim, qualquer alteração no model vai ser
			// refletida no repository.
			predicates.add(builder.like(builder.lower(root.get(Lancamento_.DESCRICAO)),
					"%" + lancamentoFilter.getDescricao().toLowerCase() + "%"));
		}

		if (lancamentoFilter.getDataVencimentoDe() != null) {
			predicates.add(builder.greaterThanOrEqualTo(root.get(Lancamento_.dataVencimento),
					lancamentoFilter.getDataVencimentoDe()));
		}

		if (lancamentoFilter.getDataVencimentoAte() != null) {
			predicates.add(builder.lessThanOrEqualTo(root.get(Lancamento_.dataVencimento),
					lancamentoFilter.getDataVencimentoAte()));
		}

		return predicates.toArray(new Predicate[predicates.size()]);
	}

}