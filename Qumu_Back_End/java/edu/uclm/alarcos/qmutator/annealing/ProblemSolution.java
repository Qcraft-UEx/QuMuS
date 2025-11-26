package edu.uclm.alarcos.qmutator.annealing;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.uclm.alarcos.qmutator.annealing.mut.AnnMutant;

@Entity
public class ProblemSolution {
	@Id @Column(length = 36) 
	private String id;
	
	@Column(columnDefinition = "LONGTEXT")
	@Lob
	private String combination;
	private Double energy;
	
	@ManyToOne
	@JoinColumn
	@JsonIgnore
	private Problem problem;

	@ManyToOne
	@JoinColumn
	@JsonIgnore
	private AnnMutant annMutant;
	
	public ProblemSolution() {
		this.id = UUID.randomUUID().toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCombination() {
		return combination;
	}

	public void setCombination(String combination) {
		this.combination = combination;
	}

	public Double getEnergy() {
		return energy;
	}

	public void setEnergy(Double energy) {
		this.energy = energy;
	}
	
	public Problem getProblem() {
		return problem;
	}
	
	public void setProblem(Problem problem) {
		this.problem = problem;
	}
	
	public AnnMutant getAnnMutant() {
		return annMutant;
	}
	
	public void setAnnMutant(AnnMutant annMutant) {
		this.annMutant = annMutant;
	}
}
