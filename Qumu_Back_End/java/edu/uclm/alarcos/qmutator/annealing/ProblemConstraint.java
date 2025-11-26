package edu.uclm.alarcos.qmutator.annealing;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class ProblemConstraint {
	@Id @Column(length = 36) 
	private String id;
	
	@Column(columnDefinition = "LONGTEXT")
	@Lob
	private String constraintText;
	
	private Double lambda;
	
	private Long pos;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn
	@JsonIgnore
	private Problem problem;
	
	public ProblemConstraint() {
		this.id = UUID.randomUUID().toString();
		this.lambda = 1.0;
	}

	public ProblemConstraint(JSONObject jso) {
		this();
		this.constraintText = jso.getString("constraintText");
		this.lambda = jso.getDouble("lambda");
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getConstraintText() {
		return constraintText;
	}

	public void setConstraintText(String constraint) {
		this.constraintText = constraint;
	}

	public Double getLambda() {
		return lambda;
	}

	public void setLambda(Double lambda) {
		this.lambda = lambda;
	}

	public Problem getProblem() {
		return problem;
	}

	public void setProblem(Problem problem) {
		this.problem = problem;
	}

	public Long getPos() {
		return pos;
	}

	public void setPos(Long pos) {
		this.pos = pos;
	}
	
	
}
