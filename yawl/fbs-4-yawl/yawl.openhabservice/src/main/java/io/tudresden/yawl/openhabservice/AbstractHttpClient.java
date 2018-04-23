package io.tudresden.yawl.openhabservice;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import static javax.ws.rs.core.UriBuilder.fromPath;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public abstract class AbstractHttpClient implements Closeable {
	protected final String host;
	protected final CloseableHttpClient httpClient;
	
	public AbstractHttpClient(String host) {	
		if(!host.startsWith("http"))
			this.host = "http://" + host;
		else
			this.host = host;
		httpClient = HttpClients.createDefault();
	}
	
	protected JsonObject post(String path, String value, ContentType contentType) {
		URI uri = fromPath(host).path(path).build();
		StringEntity entity = new StringEntity(value, contentType);
		HttpPost post = new HttpPost(uri);
		post.setEntity(entity);

		try {
			CloseableHttpResponse response = httpClient.execute(post);
			int status = response.getStatusLine().getStatusCode();
			if(status >= 300) {
				throw new RuntimeException("HTTP POST failed with: " + status);
			}
				
			return new Gson().fromJson(EntityUtils.toString(response.getEntity()), JsonObject.class);
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}
	
	protected JsonObject postJson(String path, String json) {
		return post(path, json, ContentType.APPLICATION_JSON);
	}
	
	protected JsonObject postTextPlain(String path, String text) {
		return post(path, text, ContentType.TEXT_PLAIN);
	}
	
	@Override
	public void close() throws IOException {
		httpClient.close();
	}

}
