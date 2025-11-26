package edu.uclm.alarcos.qmutator.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import edu.uclm.alarcos.qmutator.annealing.mut.AnnMutant;

@Repository
public interface AnnMutantRepository extends JpaRepository <AnnMutant, String> {

	List<AnnMutant> findByProblemId(String problemId);

	@Query(value = "select description, energy from ann_mutant where problem_id= :problemId and operator= :operator", nativeQuery = true)
	List<IDescriptionEnergy> findAlive(String problemId, String operator);

	@Transactional
	@Modifying
	void deleteByProblemId(String problemId);

	@Query(value = "select id from ann_mutant where problem_id= :problemId", nativeQuery = true)
	List<String> findIdByProblemId(String problemId);

	@Query(value = "select operator from ann_mutant where id= :mutantId", nativeQuery = true)
	String findOperatorByMutantId(String mutantId);

}
