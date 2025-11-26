package edu.uclm.alarcos.qmutator.reperdao;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.alarcos.qmutator.model.Manager;


public class ReperClient<T> {
	
	private String url;

	protected ReperClient() throws IOException {
		this.url = Manager.get().readFileAsString("reper.txt");
	}

	public List<T> findAll(String resource, String... headers) {
		try {
			try(CloseableHttpClient client = HttpClientBuilder.create().build()) {
				String url = this.url + resource;
				HttpGet get = new HttpGet(url);
				if (headers!=null) {
					for (int i=0; i<headers.length; i=i+2)
						get.addHeader(headers[i], headers[i+1]);
				}
				try(CloseableHttpResponse response = client.execute(get)) {
					int code = response.getStatusLine().getStatusCode();
					if (response.getStatusLine().getStatusCode()!=200) {
						HttpStatus status = HttpStatus.resolve(code);
						String errorMessage = response.getStatusLine().getReasonPhrase();
						throw new ResponseStatusException(status, errorMessage);
					}
					HttpEntity entity = response.getEntity();
					String responseText = EntityUtils.toString(entity);
					return buildList(responseText);
				} catch (Exception e) {
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
				}
			} catch (Exception e) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
			}
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		return null;
	}
	
	public Object sendGet(String resource, String... headers) {
		try {
			try(CloseableHttpClient client = HttpClientBuilder.create().build()) {
				HttpGet get = new HttpGet(this.url + resource);
				if (headers!=null) {
					for (int i=0; i<headers.length; i=i+2)
						get.addHeader(headers[i], headers[i+1]);
				}
				try {
					CloseableHttpResponse response = client.execute(get);
					int code = response.getStatusLine().getStatusCode();
					if (response.getStatusLine().getStatusCode()!=200) {
						HttpStatus status = HttpStatus.resolve(code);
						String errorMessage = response.getStatusLine().getReasonPhrase();
						throw new ResponseStatusException(status, errorMessage);
					}
					HttpEntity entity = response.getEntity();
					String responseText = EntityUtils.toString(entity);
					return buildResult(responseText);
				} catch (Exception e) {
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
				}
			} catch (Exception e) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
			}
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		return null;
	}
	
	private Object buildResult(String responseText) {
		// TODO Auto-generated method stub
		return null;
	}

	private List<T> buildList(String responseText) throws Exception {
		return null;
	}

	private Object fill(JSONObject jso) throws Exception {
		return null;
	}

	private Method findMethod(Class<?> clazz, String prefix, String key) {
		Method[] methods = clazz.getMethods();
		for (int i=0; i<methods.length; i++) {
			if (methods[i].getName().equalsIgnoreCase("get" + key))
				return methods[i];
		}
		return null;
	}

	public String send(String resource, Class<? extends HttpEntityEnclosingRequestBase> methodClazz, Object payload, String... headers) {
		try {
			Constructor<? extends HttpEntityEnclosingRequestBase> constructor = methodClazz.getConstructor(String.class);
			
			try(CloseableHttpClient client = HttpClientBuilder.create().build()) {
				HttpEntityEnclosingRequestBase method = constructor.newInstance(this.url + resource);
				if (headers!=null) {
					for (int i=0; i<headers.length; i=i+2)
						method.addHeader(headers[i], headers[i+1]);
				}
				try {
					if (payload!=null) {
						HttpEntity entity = new StringEntity(Jsoner.toJSON(payload).toString());
						method.setEntity(entity);
					}
					CloseableHttpResponse response = client.execute(method);
					int code = response.getStatusLine().getStatusCode();
					if (response.getStatusLine().getStatusCode()!=200) {
						HttpStatus status = HttpStatus.resolve(code);
						String errorMessage = response.getStatusLine().getReasonPhrase();
						throw new ResponseStatusException(status, errorMessage);
					}
					HttpEntity entity = response.getEntity();
					String responseText = EntityUtils.toString(entity);
					return responseText;
				} catch (Exception e) {
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
				}
			} catch (Exception e) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
			}
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		return null;
	}
}