package edu.uclm.alarcos.qmutator.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.uclm.alarcos.qmutator.model.WGate;

@Repository
public interface WGateRepository extends JpaRepository <WGate, String> {

}
