package edu.uclm.alarcos.qmutator.model;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import edu.uclm.alarcos.qmutator.dao.AnnMutantRepository;
import edu.uclm.alarcos.qmutator.dao.CircuitRepository;
import edu.uclm.alarcos.qmutator.dao.GParProblemRepository;
import edu.uclm.alarcos.qmutator.dao.MutantRepository;
import edu.uclm.alarcos.qmutator.dao.ProblemRepository;
import edu.uclm.alarcos.qmutator.dao.ProblemSolutionRepository;
import edu.uclm.alarcos.qmutator.model.operators.Operator;

@Component
public class Manager {
	private HashMap<String, List<Operator>> operatorsByFamily;
	private HashMap<String, Operator> operators;
	
	@Autowired
	private CircuitRepository circuitRepo;
	@Autowired
	private MutantRepository mutantRepo;
	@Autowired
	private ProblemRepository problemRepo;
	@Autowired
	private ProblemSolutionRepository problemSolutionRepo;
	@Autowired
	private AnnMutantRepository annMutantRepo;
	@Autowired
	private GParProblemRepository gparProblemRepo;

	private Manager() {
		this.operatorsByFamily = new HashMap<>();
		this.operators = new HashMap<>();
	}
	
	private static class ManagerHolder {
		static Manager singleton=new Manager();
	}
	
	@Bean
	public static Manager get() {
		return ManagerHolder.singleton;
	}
	
	public HashMap<String, List<Operator>> getOperatorsByFamily() {
		return this.operatorsByFamily;
	}

	public void loadOperators() {
		Reflections reflections = new Reflections("edu.uclm.alarcos.qmutator.model.operators");
		Iterator<Class<? extends Operator>> operatorClasses = reflections.getSubTypesOf(Operator.class).iterator();
			
		while (operatorClasses.hasNext()) {
			try {
				Class<? extends Operator> operatorClazz = operatorClasses.next();
				if (!Modifier.isAbstract(operatorClazz.getModifiers()) && !operatorClazz.isInterface() && 
						Operator.class.isAssignableFrom(operatorClazz)) {
					Operator operator = operatorClazz.newInstance();
					
					List<Operator> family = this.operatorsByFamily.get(operator.getFamily());
					if (family==null) {
						family = new ArrayList<>();
						operatorsByFamily.put(operator.getFamily(), family);
					}
					family.add(operator);
					operators.put(operator.getName(), operator);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (List<Operator> family :  this.operatorsByFamily.values()) {
			family.sort(new Comparator<Operator>() {
	
				@Override
				public int compare(Operator o1, Operator o2) {
					return o1.getPrintedName().compareTo(o2.getPrintedName());
				}
				
			});
		}
	}

	public CircuitRepository getCircuitRepo() {
		return circuitRepo;
	}
	
	public MutantRepository getMutantRepo() {
		return mutantRepo;
	}
	
	public ProblemRepository getProblemRepo() {
		return problemRepo;
	}
	
	public ProblemSolutionRepository getProblemSolutionRepo() {
		return problemSolutionRepo;
	}
	
	public AnnMutantRepository getAnnMutantRepo() {
		return annMutantRepo;
	}
	
	public GParProblemRepository getGparProblemRepo() {
		return gparProblemRepo;
	}
	
	public static String trim(String s) {
		s = s.replace(" ", "");
		s = s.replace("\t", "");
		s = s.replace("\n", "");
		return s;
	}

	public Operator findOperator(String operatorName) {
		return this.operators.get(operatorName);
	}

	public String readFileAsString(String fileName) throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		 try (InputStream fis = classLoader.getResourceAsStream(fileName)) {
			byte[] b = new byte[fis.available()];
			fis.read(b);
			String s = new String(b);
			return s;
		 }
	}
}
