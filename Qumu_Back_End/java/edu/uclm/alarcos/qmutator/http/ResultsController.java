package edu.uclm.alarcos.qmutator.http;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.uclm.alarcos.qmutator.dao.CircuitRepository;
import edu.uclm.alarcos.qmutator.dao.MutantRepository;
import edu.uclm.alarcos.qmutator.model.KillingMatrix;
import edu.uclm.alarcos.qmutator.model.Mutant;
import edu.uclm.alarcos.qmutator.model.Circuit;

@RestController
@RequestMapping("results")
public class ResultsController {
	@Autowired
	private CircuitRepository circuitRepo;
	@Autowired
	private MutantRepository mutantRepo;
	
	@GetMapping(value = "/get/{circuitId}")
	public KillingMatrix get(HttpServletRequest request, HttpServletResponse response, @PathVariable String circuitId) throws Exception {
		String chromeDriverPath = System.getProperty("user.home");
		String os = System.getProperty("os.name");
		
		chromeDriverPath=chromeDriverPath.replace('\\', '/');
		if (!chromeDriverPath.endsWith("/"))
			chromeDriverPath = chromeDriverPath + '/';
		chromeDriverPath = chromeDriverPath + "chromedriver";
		
		if (os.contains("Windows"))
			chromeDriverPath = chromeDriverPath + ".exe";
		
		WebDriver chrome = null;
	
		Circuit original = circuitRepo.findById(circuitId).get();
		
		JSONArray originalResult;
		if (original.getExecutionResult()==null) {
			chrome = openChrome(chromeDriverPath);
			originalResult = execute(chrome, original);
		} else {
			originalResult = new JSONArray(original.getExecutionResult());
		}
		
		List<String> mutantIds = mutantRepo.getMutantIds(circuitId);
		
		if (mutantIds.size()==0)
			throw new Exception("There are no mutants in the database. Generate first.");
		
		KillingMatrix km = new KillingMatrix();
		double killeds = 0, injureds = 0;
		Mutant mutant;
		JSONArray mutantResult;
		int numberOfMutants = mutantIds.size();
		for (int i=0; i<numberOfMutants; i++) {
			String mutantId = mutantIds.get(i);
			mutant = mutantRepo.findById(mutantId).get();
			//System.out.println("Mutante " + (i+1) + "/" + numberOfMutants + ": " + mutant.getMutantIndex());

			if (mutant.getExecutionResult()==null) {
				if (chrome==null)
					chrome = openChrome(chromeDriverPath);
				mutantResult = execute(chrome, mutant);
			} else {
				mutantResult= new JSONArray(mutant.getExecutionResult());
			}
			
			checkKilling(mutant, originalResult, mutantResult);
			if (mutant.isKilled())
				killeds++;
			if (mutant.isInjured())
				injureds++;
			
			km.add(mutant);
		}
		
		if (chrome!=null)
			chrome.quit();
		
		km.setTotal(mutantIds.size());
		km.setKilleds(killeds);
		km.setInjureds(injureds);
		km.setMS(killeds/mutantIds.size());
		km.setIS(injureds/mutantIds.size());
		return km;
	}

	private JSONArray execute(WebDriver chrome, String url) {
		chrome.get(url);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
		
		WebElement btnExport = chrome.findElement(By.id("export-button"));
		btnExport.click();
		
		WebElement copy = chrome.findElement(By.id("export-amplitudes-button"));
		copy.click();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
		
		WebElement caja = chrome.findElement(By.id("export-amplitudes-pre"));
		String texto = caja.getText();
		
		((JavascriptExecutor) chrome).executeScript("window.history.back();");
		
		JSONObject jsoResultado = new JSONObject(texto);
		JSONArray amplitudes = jsoResultado.getJSONArray("output_amplitudes");
		return amplitudes;
	}
	
	private JSONArray execute(WebDriver chrome, Circuit original) {
		String url = "https://algassert.com/quirk#circuit=" + original.getCircuit().toString();
		JSONArray amplitudes = execute(chrome, url);
		original.setExecutionResult(amplitudes.toString());
		
		circuitRepo.save(original);
		return amplitudes;
	}
	
	private JSONArray execute(WebDriver chrome, Mutant mutant) {
		String url = "https://algassert.com/quirk#circuit=" + mutant.getCircuit();
		JSONArray amplitudes = execute(chrome, url);
		mutant.setExecutionResult(amplitudes.toString());
		mutantRepo.save(mutant);
		return amplitudes;
	}

	private WebDriver openChrome(String chromeDriverPath) throws Exception {
		if (!new File(chromeDriverPath).exists())
			throw new Exception("No hay datos en la base de datos para analizar los resultados. "  +
					"Para obtenerlos, se necesita disponer del chromedriver en el directorio " + System.getProperty("user.home"));
		
		System.setProperty("webdriver.chrome.driver", chromeDriverPath);
		WebDriver chrome = new ChromeDriver();
		
		chrome.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		chrome.manage().window().setSize(new Dimension(800, 900));
		chrome.manage().window().setPosition(new Point(0, 0));
		chrome.get("https://www.google.com");
		return chrome;
	}

	private void checkKilling(Mutant mutant, JSONArray jsaOriginal, JSONArray jsaMutant) {
		if (jsaOriginal.length()!=jsaMutant.length()) {
			mutant.setInjured(false);
			mutant.setKilled(true);
			return;
		}
		
		JSONObject a, b;
		double ra, ia, rb, ib;
		for (int i=0; i<jsaOriginal.length(); i++) {
			a = jsaOriginal.getJSONObject(i);
			b = jsaMutant.getJSONObject(i);
			
			ra = a.optDouble("r"); ia = a.optDouble("i");
			rb = b.optDouble("r"); ib = b.optDouble("i");
			
			if (Math.abs(ra)!=Math.abs(rb) || Math.abs(ia)!=Math.abs(ib)) {
				mutant.setKilled(true);
				break;
			} else if (ra!= rb || ia!=ib) 
				mutant.setInjured(true);
		}
		if (mutant.isKilled())
			mutant.setInjured(false);
	}
	
}
