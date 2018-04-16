package io.tudresden.fbs.axis.util;

import org.apache.http.client.methods.HttpDelete;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import static javax.ws.rs.core.UriBuilder.fromPath;

import java.io.IOException;

public class FeedbackServiceClient extends AbstractHttpClient {
	private final Gson parser = new Gson();

	public FeedbackServiceClient(String host) {
		super(host);
	}

	/**
	 * 
	 * @return the workflow uri
	 */
	public String createWorkflow(String name, String contextUri) {
		JsonObject workflow = new JsonObject();
		workflow.addProperty("name", name);
		workflow.addProperty("context", contextUri);

		return postJson("workflows", workflow.toString())
				.get("_links").getAsJsonObject()
				.get("self").getAsJsonObject()
				.get("href").getAsString();
	}

	public void deleteWorkflow(String workflowUri) {
		try {
			httpClient.execute(new HttpDelete(fromPath(workflowUri).build()));
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	/**
	 * we support only one goal
	 * 
	 * @return goalUri
	 */
	public String createGoal(String goal, String workflowUri) {
		 JsonObject jsonGoal = parser.fromJson(goal, JsonObject.class);
		 jsonGoal.addProperty("workflow", fromPath(workflowUri).build().toString());
		 return postJson("goals", goal.toString())
				 .get("_links").getAsJsonObject()
				 .get("self").getAsJsonObject()
				 .get("href").getAsString();		
	}

}
