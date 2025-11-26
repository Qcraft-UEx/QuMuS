package edu.uclm.alarcos.qmutator.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.uclm.alarcos.qmutator.annealing.ProblemSolution;

@Repository
public interface ProblemSolutionRepository extends JpaRepository <ProblemSolution, String> {

	@Transactional
	@Modifying
	@Query(value = "delete from problem_solution where problem_id= :problemId", nativeQuery = true)
	void removeByProjectId(String problemId);

	List<ProblemSolution> findByProblemId(String problemId);

	@Transactional
	@Modifying
	@Query(value = "update problem_solution set energy=null where ann_mutant_id= :mutantId", nativeQuery = true)
	void setToNullMutantSolutionsByProjectId(String mutantId);

}
