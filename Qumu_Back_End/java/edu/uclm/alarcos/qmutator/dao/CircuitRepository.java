package edu.uclm.alarcos.qmutator.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import edu.uclm.alarcos.qmutator.model.Circuit;

@Repository
public interface CircuitRepository extends JpaRepository <Circuit, String> {

	@Query(value = "select name from circuit where name like 'random%'", nativeQuery = true)
	List<String> selectRandoms();

}
