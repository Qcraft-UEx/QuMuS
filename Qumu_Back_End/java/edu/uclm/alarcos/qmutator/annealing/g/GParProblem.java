package edu.uclm.alarcos.qmutator.annealing.g;

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
public class GParProblem {
	@Id @Column(length = 36) 
	private String id;
	private String name;
	@Column(columnDefinition = "LONGTEXT")
	@Lob
	private String description;
	
	private String variables;
	private String parameters;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "gparProblem", fetch = FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	private List<GTextRule> rules;
		
	public GParProblem() {
		this.id = UUID.randomUUID().toString();
		this.rules = new ArrayList<>();
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
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public void setRules(List<GTextRule> rules) {
		this.rules = rules;
	}
	
	public List<GTextRule> getRules() {
		return rules;
	}

	public String getVariables() {
		return variables;
	}

	public void setVariables(String variables) {
		this.variables = variables;
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public void addRule(GTextRule gtr) {
		this.rules.add(gtr);
	}
	
}
