package edu.uclm.alarcos.qmutator.dwave;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

public class Proxy {
	
	private String url;
	private String token;
	
	private Proxy() {
		try {
			JSONObject dwave = this.readFileAsJSONObject("dwave.txt");
			this.url = dwave.getString("SAPI_HOME");
			this.token = dwave.getString("SAPI_TOKEN");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new Proxy().getSolverConfiguration("hybrid_binary_quadratic_model_version2");
	}
	
	public void getSolverConfiguration(String solverId) {
		Runnable task = this.getRunnableGet(url + "solvers/remote/" + solverId, "X-Auth-Token", this.token);
		task.run();
	}
	
	public void getSolvers() {
		Runnable task = this.getRunnableGet(url + "solvers/remote", "X-Auth-Token", this.token);
		task.run();
	}
	
	public void send() {
		JSONObject jsoParams = new JSONObject().
				put("answer_mode", "histogram").
				put("num_reads", 10);
		
		JSONObject jsoData = new JSONObject().
				put("lin", "AAAAAAAA4L8AAAAAAADwPwAAAAAAAAAAAAAAAAAA+H+amZmZmZnJPwAAAAAAAPh/AAAAAAAA+H8A\r\nAAAAAAD4fwAAAAAAAPh/AAAAAAAA+H8AAAAAAAD4fwAAAAAAAPh/AAAAAAAA+H8AAAAAAAD4fwAA\r\nAAAAAPh/AAAAAAAA+H8AAAAAAAD4fwAAAAAAAPh/AAAAAAAA+H8AAAAAAAD4fwAAAAAAAPh/AAAA\r\nAAAA+H8AAAAAAAD4fwAAAAAAAPh/AAAAAAAA+H8AAAAAAAD4fwAAAAAAAPh/AAAAAAAA+H8AAAAA\r\nAAD4fwAAAAAAAPh/AAAAAAAA+H8AAAAAAAD4fwAAAAAAAPh/AAAAAAAA+H8AAAAAAAD4fwAAAAAA\r\nAPh/AAAAAAAA+H8AAAAAAAD4fwAAAAAAAPh/AAAAAAAA+H8AAAAAAAD4fwAAAAAAAPh/AAAAAAAA\r\n+H8AAAAAAAD4fwAAAAAAAPh/AAAAAAAA+H8AAAAAAAD4fwAAAAAAAPh/AAAAAAAA+H8AAAAAAAD4\r\nfwAAAAAAAPh/AAAAAAAA+H8AAAAAAAD4fwAAAAAAAPh/AAAAAAAA+H8AAAAAAAD4fwAAAAAAAPh/\r\nAAAAAAAA+H8AAAAAAAD4fwAAAAAAAPh/AAAAAAAA+H8AAAAAAAD4fwAAAAAAAPh/AAAAAAAA+H8A\r\nAAAAAAD4fwAAAAAAAPh/AAAAAAAA+H8AAAAAAAD4fwAAAAAAAPh/AAAAAAAA+H8AAAAAAAD4fwAA\r\nAAAAAPh/AAAAAAAA+H8AAAAAAAD4fwAAAAAAAPh/AAAAAAAA+H8AAAAAAAD4fwAAAAAAAPh/AAAA\r\nAAAA+H8AAAAAAAD4fwAAAAAAAPh/AAAAAAAA+H8AAAAAAAD4fwAAAAAAAPh/AAAAAAAA+H8AAAAA\r\nAAD4fwAAAAAAAPh/AAAAAAAA+H8AAAAAAAD4fwAAAAAAAPh/AAAAAAAA+H8AAAAAAAD4fwAAAAAA\r\nAPh/AAAAAAAA+H8AAAAAAAD4fwAAAAAAAPh/AAAAAAAA+H8AAAAAAAD4fwAAAAAAAPh/AAAAAAAA\r\n+H8AAAAAAAD4fwAAAAAAAPh/AAAAAAAA+H8AAAAAAAD4fwAAAAAAAPh/AAAAAAAA+H8AAAAAAAD4\r\nfwAAAAAAAPh/AAAAAAAA+H8AAAAAAAD4fwAAAAAAAPh/AAAAAAAA+H8AAAAAAAD4fwAAAAAAAPh/\r\nAAAAAAAA+H8AAAAAAAD4fwAAAAAAAPh/AAAAAAAA+H8AAAAAAAD4fwAAAAAAAPh/AAAAAAAA+H8A\r\nAAAAAAD4fwAAAAAAAPh/AAAAAAAA+H8AAAAAAAD4fwAAAAAAAPh/AAAAAAAA+H8AAAAAAAD4fw==").
				put("quad", "AAAAAAAA8L8AAAAAAADgP5qZmZmZmem/").
				put("format", "qp");
		
		JSONObject jsoProblem = new JSONObject().
				put("type", "ising").
				put("solver", "hybrid_discrete_quadratic_model_version1").
				put("params", jsoParams).
				put("data", jsoData);
		
		JSONArray jsaProblems = new JSONArray().
				put(jsoProblem);
		
		String url = this.url + "problems";
		
		Runnable task = this.getRunnablePost(url, jsaProblems, "X-Auth-Token", this.token);
		task.run();
	}
	
	public Runnable getRunnablePost(String url, Object payload, String... headers) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				HttpClientBuilder builder = HttpClientBuilder.create();
				CloseableHttpClient client = builder.build();
				HttpPost request = new HttpPost(url);
				for (int i=0; i<headers.length; i++)
					request.addHeader(headers[i], headers[++i]);
				System.out.println(payload.toString());
				try {
					StringEntity requestEntity = new StringEntity(payload.toString());
					request.setEntity(requestEntity);
					try {
						StringResponseHandler responseHandler = new StringResponseHandler();
						Object executionResult = client.execute(request, responseHandler);
						System.out.println(executionResult);
					} finally {
						client.close();
					}
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		};
		return runnable;
	}
	
	public Runnable getRunnableGet(String url, String... headers) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				HttpClientBuilder builder = HttpClientBuilder.create();
				CloseableHttpClient client = builder.build();
				HttpGet request = new HttpGet(url);
				for (int i=0; i<headers.length; i++)
					request.addHeader(headers[i], headers[++i]);
				try {
					try {
						StringResponseHandler responseHandler = new StringResponseHandler();
						Object executionResult = client.execute(request, responseHandler);
						System.out.println(executionResult);
					} finally {
						client.close();
					}
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		};
		return runnable;
	}
	
	public JSONObject readFileAsJSONObject(String fileName) throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		 try (InputStream fis = classLoader.getResourceAsStream(fileName)) {
			byte[] b = new byte[fis.available()];
			fis.read(b);
			String s = new String(b);
			return new JSONObject(s);
		 }
	}
	
	private static class ProxyHolder {
		static Proxy singleton = new Proxy();
	}
	
	public static Proxy get() {
		return ProxyHolder.singleton;
	}
}
