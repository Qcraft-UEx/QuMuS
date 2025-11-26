package edu.uclm.alarcos.qmutator.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import edu.uclm.alarcos.qmutator.annealing.g.GParProblem;
import edu.uclm.alarcos.qmutator.http.IProblemDescription;

@Repository
public interface GParProblemRepository extends JpaRepository <GParProblem, String> {

	@Query(value = "select id, name from gpar_problem order by name", nativeQuery = true)
	List<IProblemDescription> getNames();

}
