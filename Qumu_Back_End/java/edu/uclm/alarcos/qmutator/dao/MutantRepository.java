package edu.uclm.alarcos.qmutator.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import edu.uclm.alarcos.qmutator.model.Mutant;

@Repository
public interface MutantRepository extends JpaRepository <Mutant, String> {

	List<Mutant> findByOriginalCircuitId(String originalCircuitId);

	@Query(value = "select id from mutant where original_circuit_id=:circuitId order by mutant_index", nativeQuery=true)
	List<String> getMutantIds(@Param("circuitId") String circuitId);

	@Transactional
	@Modifying
	@Query(value = "delete from mutant where original_circuit_id=:circuitId", nativeQuery=true)
	void deleteByOriginalCircuitId(@Param("circuitId") String circuitId);

	@Query(value = "select mutant_index from mutant where original_circuit_id=:circuitId", nativeQuery=true)
	List<Integer> getMutantIndexes(@Param("circuitId") String circuitId);
	
	@Query(value = "select mutant_index from mutant where original_circuit_id=:circuitId and col_index in (:mutantsToUse)", nativeQuery=true)
	List<Integer> getMutantIndexesFiltering(@Param("circuitId") String circuitId, @Param("mutantsToUse") int[] mutantsToUSe);

	@Query(value = "select * from mutant where original_circuit_id=:circuitId and mutant_index=:mutantIndex", nativeQuery=true)
	Mutant getMutant(@Param("circuitId") String circuitId, @Param("mutantIndex") int mutantIndex);

	@Query(value = "select mutant_index from mutant where original_circuit_id=:circuitId and col_index in (:colIndexes) order by mutant_index", nativeQuery = true)
	List<Integer> getMutantIndexes(@Param("circuitId") String circuitId, @Param("colIndexes") String colIndexes);
}
