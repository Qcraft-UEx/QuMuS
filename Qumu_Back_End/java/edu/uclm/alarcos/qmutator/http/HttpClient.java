package edu.uclm.alarcos.qmutator.http;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class HttpClient {
	
	public String sendPost(String url, JSONArray headers, JSONObject payload) {
		try(CloseableHttpClient client = HttpClientBuilder.create().build()) {
			HttpPost post = new HttpPost(url);
			try {
				HttpEntity entity = new StringEntity(payload.toString());
				post.setEntity(entity);
				if (headers!=null)
					for (int i=0; i<headers.length(); i++) {
						String header = headers.getString(i);
						String headerName = header.substring(0, header.indexOf(':')).trim();
						String headerValue = header.substring(header.indexOf(':')+1).trim();
						post.setHeader(headerName, headerValue);
					}
				
				CloseableHttpResponse response = client.execute(post);
				int code = response.getStatusLine().getStatusCode();
				if (response.getStatusLine().getStatusCode()!=200) {
					HttpStatus status = HttpStatus.resolve(code);
					String errorMessage = response.getStatusLine().getReasonPhrase();
					throw new ResponseStatusException(status, errorMessage);
				}
				entity = response.getEntity();
				String responseText = EntityUtils.toString(entity);
				return responseText;
			} catch (Exception e) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
			}
		} catch (IOException e1) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e1.getMessage());
		}
	}
}