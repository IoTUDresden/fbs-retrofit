package io.tudresden.fbs.axis.util;

import static java.lang.String.format;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import static javax.ws.rs.core.UriBuilder.fromPath;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.jersey.media.sse.EventInput;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import io.tudresden.fbs.axis.beans.FeedbackResponse;

public class Compensation {
	private static final Log LOG = LogFactory.getLog(Compensation.class);
	
	private final String contextUri;
	private final String serviceUri;
	
	private final Gson parser = new Gson();
	
	private boolean hasBeenSatisfied = false;
	private boolean hasBeenFinished = true;
	
	private String workflowName;
	private String workflowUri;
	private String goalUri;
	private String goal;
	
	private FeedbackResponse response;
	
	private FeedbackServiceClient httpClient;
	private Client sseClient;
	
	private CountDownLatch waitForEvent;
	
	public Compensation(String serviceUri, String contextUri) {
		this.serviceUri = serviceUri;
		this.contextUri = contextUri;
	}
	
	/**
	 * This will do the compensation and block, till the compensation was finished.
	 */
	public FeedbackResponse work() {
		waitForEvent = new CountDownLatch(1);

		prepare();
		LOG.info("Start compensating feedback loop");

		httpClient.createWorkflow(workflowName, contextUri);
		LOG.info(format("Workflow %s created", workflowUri));

		waitForEvent();
		goalUri = httpClient.createGoal(goal, workflowUri);
		LOG.info(format("Goal %s created", goalUri));
		LOG.info("Goals created. Waiting for feedback loop");

		try {
			waitForEvent.await();
			LOG.info(format("Workflow done. Satisfaction is %s", hasBeenSatisfied));
		} catch (InterruptedException e) {
			LOG.error("I'm interrupted...", e);
		} finally {
			httpClient.deleteWorkflow(workflowUri);
			finish();
		}
		
		return response;
	}
	
	
	private void prepare() {
		httpClient = new FeedbackServiceClient(serviceUri);
		sseClient = ClientBuilder.newBuilder().register(SseFeature.class).build();
	}
	
	private void finish() {
		try {
			httpClient.close();
			sseClient.close();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}
	
	private void waitForEvent() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				EventInput eventInput = sseClient
						.target(fromPath(serviceUri).path("events").path(fromPath(workflowUri).build().getPath()))
						.request().get(EventInput.class);

				while (!eventInput.isClosed()) {
					final InboundEvent event = eventInput.read();
					if (workflowHasBeenDone(event.readData()))
						break;
				}

				response = new FeedbackResponse(hasBeenFinished, hasBeenSatisfied);
				waitForEvent.countDown();

			}
		}).start();
	}	
	
	private boolean workflowHasBeenDone(String json) {
		JsonObject workflow = parser.fromJson(json, JsonObject.class);
		hasBeenSatisfied = workflow.get("hasBeenSatisfied").getAsBoolean();
		hasBeenFinished = workflow.get("hasBeenFinished").getAsBoolean();
		return hasBeenFinished || hasBeenSatisfied;
	}



	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}

	public void setGoal(String goal) {
		this.goal = goal;
	}

}
