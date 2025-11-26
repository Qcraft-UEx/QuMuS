package edu.uclm.alarcos.qmutator.annealing.g;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class GTextRule {
	@Id
	private String id;
	private Double lambda;
	@Column(columnDefinition = "LONGTEXT")
	@Lob
	private String text;
	@Lob
	private String mathML;
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn
	@JsonIgnore
	private GParProblem gparProblem;
	
	public GTextRule() {
		this.id = UUID.randomUUID().toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Double getLambda() {
		return lambda;
	}

	public void setLambda(Double lambda) {
		this.lambda = lambda;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public String getMathML() {
		return mathML;
	}
	
	public void setMathML(String mathML) {
		this.mathML = mathML;
	}

	@JsonIgnore
	public GParProblem getGparProblem() {
		return gparProblem;
	}

	public void setGparProblem(GParProblem gparProblem) {
		this.gparProblem = gparProblem;
	}
}
