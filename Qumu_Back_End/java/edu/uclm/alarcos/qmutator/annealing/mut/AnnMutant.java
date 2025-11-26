package edu.uclm.alarcos.qmutator.annealing.mut;

import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.uclm.alarcos.qmutator.annealing.Combination;
import edu.uclm.alarcos.qmutator.annealing.CH;
import edu.uclm.alarcos.qmutator.annealing.Problem;
import edu.uclm.alarcos.qmutator.annealing.ProblemSolution;

@Entity
public class AnnMutant {
	@Id @Column(length = 36)
	private String id;
	
	@Column(columnDefinition = "LONGTEXT")
	@Lob
	private String concreteFunction;
	
	private String operator;
	
	@ManyToOne
	@JoinColumn
	@JsonIgnore
	private Problem problem;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "annMutant", fetch = FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	private List<ProblemSolution> bestSolutions;

	@Column(columnDefinition = "LONGTEXT")
	@Lob
	private String description;

	private boolean killed;

	private Double energy;
	
	public AnnMutant() {
		this.id = UUID.randomUUID().toString();
	}

	public void setConcreteFunction(CH f) {
		this.concreteFunction = f.toJSON().toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getConcreteFunction() {
		return concreteFunction;
	}

	public void setConcreteFunction(String concreteFunction) {
		this.concreteFunction = concreteFunction;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}
	
	public void setProblem(Problem problem) {
		this.problem = problem;
	}
	
	public Problem getProblem() {
		return problem;
	}

	public double evaluate(Combination combination) throws Exception {
		JSONObject jsoCF = new JSONObject(this.concreteFunction);
		CH cf = new CH(jsoCF);
		return cf.calculate(combination);
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}

	public void setKilled(boolean killed) {
		this.killed = true;
	}
	
	public boolean isKilled() {
		return killed;
	}

	public void setEnergy(double energy) {
		this.energy = energy;
	}
	
	public Double getEnergy() {
		return energy;
	}
}
