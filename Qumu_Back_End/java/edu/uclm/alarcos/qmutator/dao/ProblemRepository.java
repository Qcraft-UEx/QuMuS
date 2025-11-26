package edu.uclm.alarcos.qmutator.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import edu.uclm.alarcos.qmutator.annealing.Problem;
import edu.uclm.alarcos.qmutator.http.IProblemDescription;

@Repository
public interface ProblemRepository extends JpaRepository <Problem, String> {

	@Query(value = "select id, name from problem order by name", nativeQuery = true)
	List<IProblemDescription> getProblems();

}
