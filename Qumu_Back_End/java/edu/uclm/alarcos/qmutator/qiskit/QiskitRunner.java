package edu.uclm.alarcos.qmutator.qiskit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.uclm.alarcos.qmutator.model.Manager;

public class QiskitRunner {

	private String workingFolder;
	
	public QiskitRunner() {
		this.workingFolder = System.getProperty("user.home");
		this.workingFolder = this.workingFolder.replace('\\', '/');
		if (!this.workingFolder.endsWith("/"))
			this.workingFolder = this.workingFolder + "/";
		this.workingFolder = this.workingFolder + "qumu/";
		GregorianCalendar gc = (GregorianCalendar) GregorianCalendar.getInstance();
		this.workingFolder = this.workingFolder + new SecureRandom().nextInt(100000) + "." + 
				gc.get(Calendar.YEAR) + "-" + (1+gc.get(Calendar.MONTH)) + "-" +
				gc.get(Calendar.DATE) + "/";
		new File(this.workingFolder).mkdirs();
	}

	public JSONObject execute(String fileName, String code, int shots) {
		JSONObject executionResult = new JSONObject();
		long timeIni = System.currentTimeMillis();
		
		code = code.replace("#SHOTS#", "" + shots);
		try(FileOutputStream fos = new FileOutputStream(this.workingFolder + fileName)) {
			fos.write(code.getBytes());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		executionResult.put("codeGenerationTime", System.currentTimeMillis()-timeIni);
		
		timeIni = System.currentTimeMillis();
		
		String[] commands = {
				"bash", "-c",
				"python " + fileName
			};
		
		ProcessBuilder pb = new ProcessBuilder(commands);
		Map<String, String> env = pb.environment();
		try {
			env.put("PATH", Manager.get().readFileAsString("pythonPath.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		File file = new File(this.workingFolder);
		pb.directory(file);
		
		File outputFile = new File(this.workingFolder + "output.txt");
		File errorsFile = new File(this.workingFolder + "errors.txt");
		pb.redirectOutput(outputFile);
		pb.redirectError(errorsFile);
		
		Process process;
		int returnCode = 0;
		try {
			process = pb.start();
			returnCode=process.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
		executionResult.put("codeExecutionTime", System.currentTimeMillis()-timeIni);
		
		timeIni = System.currentTimeMillis();
		
		JSONArray outputLines=new JSONArray();
		try(FileReader fr=new FileReader(outputFile)) {  
			try(BufferedReader br=new BufferedReader(fr)) {
				String line=null;
				while ((line=br.readLine())!=null)
					outputLines.put(line);
			}
		} catch (Exception e) {
			executionResult.put("returnCode", -1);
			executionResult.put("errorLines", new JSONArray().put(e.getMessage()));
		} 

		JSONArray errorLines=new JSONArray();
		try(FileReader fr=new FileReader(errorsFile)) {
			try(BufferedReader br=new BufferedReader(fr)) {
				String line=null;
				while ((line=br.readLine())!=null)
					errorLines.put(line);
			}
		} catch (Exception e) {
			executionResult.put("returnCode", -1);
			executionResult.put("errorLines", new JSONArray().put(e.getMessage()));
		}
		
		executionResult.put("returnCode", returnCode);
		if (outputLines.length()>0) {
			JSONObject jsoOuputLines = new JSONObject(outputLines.getString(0));
			outputLines.remove(0);

			ArrayList<JSONObject> results = new ArrayList<>();
			Iterator<String> keys = jsoOuputLines.keys();
			while (keys.hasNext()) {
				String key = keys.next();
				int frequency = jsoOuputLines.getInt(key);
				JSONObject jso = new JSONObject();
				jso.put("order", Integer.parseInt(key, 2));
				jso.put("qubits", key);
				jso.put("frequency", frequency);
				results.add(jso);
			}
			Collections.sort(results, (o1, o2) -> o1.getInt("order")-o2.getInt("order"));
			
			outputLines = new JSONArray(results);
		}
		executionResult.put("outputLines", outputLines);
		executionResult.put("errorLines", errorLines);
		executionResult.put("resultsPreparationTime", System.currentTimeMillis() - timeIni);
		
		new File(this.workingFolder + fileName).delete();
		
		return executionResult;
	}

	public String getWorkingFolder() {
		return workingFolder;
	}
}
