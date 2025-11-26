package edu.uclm.alarcos.qmutator.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.uclm.alarcos.qmutator.model.gates.AControlGate;
import edu.uclm.alarcos.qmutator.model.gates.CallableGate;
import edu.uclm.alarcos.qmutator.model.gates.Gate;
import edu.uclm.alarcos.qmutator.model.gates.complex.Matrix;
import edu.uclm.alarcos.qmutator.model.gates.complex.UserDefinedGate;
import edu.uclm.alarcos.qmutator.model.gates.primitive.Gate1;
import edu.uclm.alarcos.qmutator.model.gates.primitive.GateNoOperation;

@Entity
public class Circuit {
	@Id
	@Column(length = 36)
	private String id;
	private String name;
	
	@Column(columnDefinition = "LONGTEXT")
	@Lob
	private String circuit;

	private Integer numberOfColumns;
	
	@Transient
	private InitColumn initColumn;
	@Transient
	private List<QColumn> columns;
	
	@Transient
	private List<UserDefinedGate> userDefinedGates;
	
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private Circuit containerCircuit;
	
	@Transient
	private Map<String, UserDefinedGate> gatesById;	
	@Transient
	private Map<String, UserDefinedGate> gatesByName;
	@Transient
	protected Map<Integer, ArrayList<Integer>> mutablePositions;
	
	
	@Column(columnDefinition = "LONGTEXT")
	@Lob
	private String executionResult;
	private int qubits;
	
	private String outputQubits;
	
	@Lob
	private String qiskitCode;
	@Lob
	private String qiskitResult;
		
	public Circuit() {
		this.id = UUID.randomUUID().toString();
		this.columns = new ArrayList<>();
		this.userDefinedGates = new ArrayList<>();
		this.gatesById = new HashMap<>();
		this.gatesByName = new HashMap<>();
		this.mutablePositions = new HashMap<>();
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
	
	public String getCircuit() {
		return this.circuit;
	}
	
	public void setCircuit(String circuit) throws Exception {
		this.circuit = Manager.trim(circuit);
		this.load(circuit);
	}
	
	private void load(String circuit) throws Exception {
		JSONObject jsoQuirk = new JSONObject(circuit);
		JSONArray jsa = jsoQuirk.optJSONArray("gates");
		this.loadGates(jsa);
		jsa = jsoQuirk.getJSONArray("cols");
		this.loadColumns(jsa);
		this.numberOfColumns = this.columns.size();
		jsa = jsoQuirk.optJSONArray("init");
		this.loadInitColumn(jsa);
	}

	private void loadInitColumn(JSONArray jsaInitColumn) {
		if (jsaInitColumn==null)
			return;
		this.initColumn = new InitColumn();
		this.initColumn.setContainerCircuit(this);
		this.initColumn.load(jsaInitColumn);
	}

	private void loadGates(JSONArray jsaGates) throws Exception {
		if (jsaGates==null)
			return;
		
		JSONObject jsoGate;
		for (int i=0; i<jsaGates.length(); i++) {
			jsoGate = jsaGates.getJSONObject(i);
			String quirkId = jsoGate.getString("id");
			String name = jsoGate.optString("name");

			JSONObject jsoCircuit = jsoGate.optJSONObject("circuit");
			if (jsoCircuit!=null) { 
				Circuit subcircuit = new Circuit();
				subcircuit.setCircuit(jsoCircuit.toString());
				CallableGate gate = new CallableGate();
				gate.setQuirkId(quirkId);
				gate.setName(name);
				gate.setCircuit(subcircuit);
				this.userDefinedGates.add(gate);
				this.gatesById.put(quirkId, gate);
				this.gatesByName.put(name, gate);
			}
			String sMatrix = jsoGate.optString("matrix");
			if (sMatrix.length()>0) {
				Matrix matrix = new Matrix();
				matrix.setQuirkId(quirkId);
				matrix.setName(name);
				matrix.loadMatrix(sMatrix);
				
				this.userDefinedGates.add(matrix);
				this.gatesById.put(quirkId, matrix);
				this.gatesByName.put(name, matrix);
				
				matrix.setContainerCircuit(this);
			}
		}
	}

	private void loadColumns(JSONArray jsaCols) throws Exception {
		this.qubits = 0;
		for (int i=0; i<jsaCols.length(); i++) {
			JSONArray jsaColumn = jsaCols.getJSONArray(i);
			QColumn column = new QColumn();
			column.setContainerCircuit(this);
			column.setPos(i);
			column.load(jsaColumn);
			this.columns.add(column);
			column.setQubits(jsaColumn.length());
			if (jsaColumn.length()>this.qubits)
				this.qubits = jsaColumn.length();
		}
	}
	
	@Override
	public String toString() {
		JSONObject jso = this.toJSON();
		return jso.toString();
	}

	public JSONObject toJSON() {
		JSONObject jso = new JSONObject();
		jso.put("id", this.name);
		JSONArray jsaCols = new JSONArray();
		for (QColumn column : this.columns)
			jsaCols.put(column.toJSON());
		jso.put("cols", jsaCols);
		
		List<UserDefinedGate> gates = this.userDefinedGates;
		if  (!gates.isEmpty()) {
			JSONArray jsaGates = new JSONArray();
			for (UserDefinedGate gate : gates) 
				jsaGates.put(gate.toJSON());
			jso.put("gates", jsaGates);
		}
		
		if (initColumn!=null && initColumn.size()>0)
			jso.put("init", this.initColumn.toJSON());

		return jso;
	}
	
	public InitColumn getInitColumn() {
		return initColumn;
	}
		
	public UserDefinedGate findCustomizableGate(String gateName) {
		return this.gatesByName.get(gateName);
	}

	public List<QColumn> getColumns() {
		return this.columns;
	}
	
	public String getExecutionResult() {
		return executionResult;
	}
	
	public void setExecutionResult(String executionResult) {
		this.executionResult = executionResult;
	}

	public List<CallableGate> getCallableGates() {
		List<CallableGate> result = new ArrayList<>();
		for (UserDefinedGate gate : this.userDefinedGates)
			if (CallableGate.class.isAssignableFrom(gate.getClass()))
				result.add((CallableGate) gate);
		return result;
	}

	public void setInitColumn(InitColumn initColumn) {
		this.initColumn = initColumn;
	}

	public void removeColumn(int columnIndex) {
		this.columns.remove(columnIndex);
	}

	public void removeGate(int columnIndex, int wireIndex) {
		QColumn column = this.columns.get(columnIndex);
		Gate1 gate1 = new Gate1();
		gate1.setColumn(column);
		gate1.setQubit(wireIndex);
		column.setGate(wireIndex, gate1);
	}

	public void swapColumns(int from, int to) {
		QColumn column = this.columns.get(from);
		this.columns.add(to+1, column);
		this.columns.remove(from);
	}

	public void setGate(Integer columnIndex, Gate newGate) {
		QColumn column = this.getColumns().get(columnIndex);
		
		Gate oldGate = column.getGates().get(newGate.getQubit());
		column.setGate(newGate.getQubit(), newGate);
		
		if (column.getControlledGate()!=null && column.getControlledGate()==oldGate) {
			column.setControlledGate(newGate);
			AControlGate controlGate;
			for (int i=0; i<column.getControlGates().size(); i++) {
				controlGate = column.getControlGates().get(i);
				controlGate.setControlledGate(newGate);
			}
		}		
	}

	public void addColumn(int columnIndex, QColumn newColumn) {
		if (columnIndex==this.getColumns().size()) {
			this.columns.add(newColumn);
			newColumn.setPos(this.columns.size());
		} else {
			this.columns.add(columnIndex+1, newColumn);
			newColumn.setPos(columnIndex+1);
		}
	}

	public void swapGates(int columnIndex, int a, int b) {
		QColumn column = this.getColumns().get(columnIndex);
		Gate gateA = column.getGates().get(a);
		Gate gateB =  column.getGates().get(b);
		
		column.getGates().set(b, gateA);
		column.getGates().set(a, gateB);
	}
	
	public void moveGate(int columnIndex, int a, int b) {
		QColumn column = this.getColumns().get(columnIndex);
		Gate gateA = column.getGates().get(a);
		
		while (b>=column.getGates().size()) {
			column.add(new Gate1());
		} 
		column.getGates().set(b, gateA);
		column.getGates().set(a, new GateNoOperation());
	}

	public String prepareQiskitCode() throws Exception {
		String template = Manager.get().readFileAsString("qiskitTemplate.txt");
		
		StringBuilder sb = new StringBuilder();
		if (this.initColumn!=null) {
			sb = sb.append(this.initColumn.buildQiskitCode());
			template = template.replace("#INITIALIZATION#", "#INITIALIZATION#\n" + sb.toString());
		}
		sb = new StringBuilder();
		QColumn column;
		for (int i=0; i<this.columns.size(); i++) {
			column = this.columns.get(i);
			sb = sb.append(column.buildQiskitCode());
			sb = sb.append("\n");			
		}
		
		int qubits = this.getQubits();
		template = template.replace("#CODE#", "#CODE#\n" + sb.toString());
		template = template.replace("#QUBITS#", "" + qubits);
		
		sb = new StringBuilder();
		String[] tokens = this.outputQubits.split(",");
		for (int i=0; i<tokens.length; i++) {
			String token = tokens[i].trim();
			sb.append("circuit.measure(qreg[" + token + "], creg[" + token + "])\n");
		}
		template = template.replace("#MEASURES#", "#MEASURES\n" + sb.toString());
		template = template.replace("#OUTPUT_QUBITS#", "" + tokens.length);
		return template;
	}
	
	public String replaceMeasures() throws Exception {
		String[] tokens = this.outputQubits.split(",");
		this.qiskitCode = this.qiskitCode.replace("#OUTPUT_QUBITS#", "" + tokens.length);
		
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<tokens.length; i++) {
			String token = tokens[i].trim();
			sb.append("circuit.measure(qreg[" + token + "], creg[" + token + "])\n");
		}
		this.qiskitCode = this.qiskitCode.replace("#MEASURES#", "#MEASURES#\n" + sb.toString());
		return this.qiskitCode;
	}

	public int getQubits() {
		return qubits;
	}

	public void setQubits(int qubits) {
		this.qubits = qubits;
	}
	
	public void setQiskitCode(String qiskitCode) {
		this.qiskitCode = qiskitCode;
	}
	
	public String[] getQiskitCode() {
		if (qiskitCode!=null)
			return qiskitCode.split("\n");
		return null;
	}
	
	@JsonIgnore
	public JSONObject getQiskitResult() {
		return new JSONObject(this.qiskitResult);
	}
	
	public void setQiskitResult(String qiskitResult) {
		this.qiskitResult = qiskitResult;
	}

	public void addMutablePosition(int columnIndex, int... rowIndexes) {
		ArrayList<Integer> column = this.mutablePositions.get(columnIndex);
		if (column==null) {
			column = new ArrayList<>();
			this.mutablePositions.put(columnIndex, column);
		}
		for (int i=0; i<rowIndexes.length; i++)
			column.add(rowIndexes[i]);
	}
	
	public void addMutablePosition(int columnIndex, List<Integer> rowIndexes) {
		ArrayList<Integer> column = this.mutablePositions.get(columnIndex);
		if (column==null) {
			column = new ArrayList<>();
			this.mutablePositions.put(columnIndex, column);
		}
		for (int i=0; i<rowIndexes.size(); i++)
			column.add(rowIndexes.get(i));
	}

	public void clearMutablePositions() {
		this.mutablePositions.clear();
	}

	@JsonIgnore
	public Map<Integer, ArrayList<Integer>> getMutablePositions() {
		return this.mutablePositions;
	}

	@Transient
	public void setContainerCircuit(Circuit containerCircuit) {
		this.containerCircuit = containerCircuit;
	}
	
	@JsonIgnore
	public Circuit getContainerCircuit() {
		return containerCircuit;
	}

	public QColumn getColumn(Integer index) {
		return this.columns.get(index);
	}
	
	public void setColumns(List<QColumn> columns) {
		this.columns = columns;
	}
	
	public Integer getNumberOfColumns() {
		return numberOfColumns;
	}
	
	public void setNumberOfColumns(Integer numberOfColumns) {
		this.numberOfColumns = numberOfColumns;
	}
	
	public Map<String, UserDefinedGate> getGatesById() {
		return gatesById;
	}

	public void setGatesById(Map<String, UserDefinedGate> gatesById) {
		this.gatesById = gatesById;
	}

	public Map<String, UserDefinedGate> getGatesByName() {
		return gatesByName;
	}

	public void setGatesByName(Map<String, UserDefinedGate> gatesByName) {
		this.gatesByName = gatesByName;
	}

	public void setMutablePositions(Map<Integer, ArrayList<Integer>> mutablePositions) {
		this.mutablePositions = mutablePositions;
	}

	public void replaceGate(int rowIndex, CallableGate newGate) {
		this.userDefinedGates.set(rowIndex, newGate);
	}

	public List<WGate> getGates() {
		ArrayList<WGate> gates = new ArrayList<>();
		QColumn column;
		for (int i=0; i<this.columns.size(); i++) {
			column = this.columns.get(i);
			for (int j=0; j<column.getGates().size(); j++) {
				Gate gate = column.getGates().get(j);
				WGate wgate = new WGate();
				wgate.setOriginalCircuit(this);
				wgate.setGateName(gate.getName().toString());
				wgate.setColIndex(i);
				wgate.setRowIndex(j);
				gates.add(wgate);
			}
		}
		return gates;
	}

	public String getOutputQubits() {
		return outputQubits;
	}
	
	public void setOutputQubits(String outputQubits) {
		this.outputQubits = outputQubits;
	}
}
