package edu.uclm.alarcos.qmutator.annealing;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
public class Problem {
	@Id @Column(length = 36) 
	private String id;
	private String name;
	@Column(columnDefinition = "LONGTEXT")
	@Lob
	private String description;
	
	@Column(columnDefinition = "LONGTEXT")
	@Lob
	private String concreteFunction;
	
	@Column(columnDefinition = "LONGTEXT")
	@Lob
	private String minFunction;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "problem", fetch = FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	private List<ProblemConstraint> constraints;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "problem", fetch = FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	private List<ProblemSolution> bestSolutions;
	
	public Problem() {
		this.id = UUID.randomUUID().toString();
		this.constraints = new ArrayList<>();
		this.bestSolutions = new ArrayList<>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMinFunction() {
		return minFunction;
	}

	public void setMinFunction(String minFunction) {
		this.minFunction = minFunction;
	}

	public List<ProblemConstraint> getConstraints() {
		return this.constraints;
	}

	public void setConstraints(List<ProblemConstraint> constraints) {
		this.constraints = constraints;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public void add(ProblemConstraint pc) {
		this.constraints.add(pc);
	}

	public List<ProblemSolution> getBestSolutions() {
		return bestSolutions;
	}

	public void setBestSolutions(List<ProblemSolution> bestSolutions) {
		this.bestSolutions = bestSolutions;
	}
	
	public void add(ProblemSolution problemSolution) {
		this.bestSolutions.add(problemSolution);
	}
	
	public String getConcreteFunction() {
		return concreteFunction;
	}
	
	public void setConcreteFunction(String concreteFunction) {
		this.concreteFunction = concreteFunction;
	}
}
