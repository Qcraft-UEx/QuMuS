package edu.uclm.alarcos.qmutator.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

@Repository
public class GeneralRepository {

	@PersistenceContext
	EntityManager em;
	
	public List<Integer> getIntegerList(String sql) {
		List<Integer> result = new ArrayList<>();
		Query query = this.em.createNativeQuery(sql);
		List queryResults = query.getResultList();
		return queryResults;
	}
}
