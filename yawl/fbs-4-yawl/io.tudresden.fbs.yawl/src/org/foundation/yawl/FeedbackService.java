package org.foundation.yawl;

import static java.lang.String.format;
import static javax.ws.rs.core.UriBuilder.fromPath;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.glassfish.jersey.media.sse.EventInput;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawl.elements.data.YParameter;
import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;
import org.yawlfoundation.yawl.engine.interfce.interfaceB.InterfaceBWebsideController;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Service for running feedback loop.
 * 
 * @author andre
 *
 */
public class FeedbackService extends InterfaceBWebsideController {
	private static final Logger LOG = LoggerFactory.getLogger(FeedbackService.class);

	private static final String PWD = "1234";
	private static final String USER = "feedbackService";

	private static final String GOAL = "Goal";
	private static final String CONTEXT_URI = "ContextUri";
	private static final String SERVICE_URI = "FeedbackServiceUri";
	private static final String WORKFLOW_NAME = "WorkflowName";
	private static final String HAS_BEEN_SATISFIED = "HasBeenSatisfied";
	private static final String HAS_BEEN_FINISHED = "HasBeenFinished";

	private String handle;
	private String workflowUri;
	private WorkItemRecord workItemRecord;
	private String instanceId;
	private String serviceUri;

	private boolean hasBeenSatisfied = false;
	private boolean hasBeenFinished = true;
	private boolean wasCanceled = false;

	private CloseableHttpClient httpClient;
	private Client sseClient;
	private CountDownLatch waitForEvent;

	private final Gson parser = new Gson();

	/**
	 * This method is invoked when an enabled or executing work item is
	 * cancelled by the engine (e.g. due to a cancellation occurring within the
	 * process control- ow). In principle, after this method has been invoked, a
	 * Custom Service should not attempt to check-out or check-in the work item,
	 * and if the work item is in progress, its processing should be stopped.
	 */
	@Override
	public void handleCancelledWorkItemEvent(WorkItemRecord wir) {
		LOG.info("################ canceled ###################");
		wasCanceled = true;
		if (waitForEvent != null)
			waitForEvent.countDown();
	}
	

	/**
	 * This method is the endpoint of an Engine enabled work item event noti
	 * cation; that is, it is called when the Engine creates a new work item and
	 * places it in the enabled state, and the Custom Service containing the
	 * method is the one associated at design time with the work item.
	 */
	@Override
	public void handleEnabledWorkItemEvent(WorkItemRecord wir) {
		if (wasCanceled)
			return;
		LOG.info("################ handleEnabledWorkItemEvent ###################");

		instanceId = wir.getUniqueID();
		try {

			// connect only if not already connected
			if (!connected())
				handle = connect(USER, PWD);
			// handle = connect(engineLogonName, engineLogonPassword);

			// checkout ... process ... checkin
			workItemRecord = checkOut(wir.getID(), handle);
			wir = workItemRecord;

			work();
			if (wasCanceled)
				return;

			// somewhere in the docs it says, it is possible that the session is
			// expired, if the service takes too long
			// so we just get a new session
			if (!connected())
				handle = connect(USER, PWD);
			checkInWorkItem(wir.getID(), wir.getDataList(), getOutputData(), null, handle);

		} catch (Exception ioe) {
			ioe.printStackTrace();
		}

	}

	private void work() {
		waitForEvent = new CountDownLatch(1);

		prepare();
		LOG.info("Start compensating feedback loop");

		createWorkflow();
		LOG.info(String.format("Workflow %s created", workflowUri));

		waitForEvent();
		createGoals();
		LOG.info("Goals created. Waiting for feedback loop");

		try {
			waitForEvent.await();
			if (wasCanceled)
				LOG.info("Workflow canceled.");
			else
				LOG.info(format("Workflow done. Satisfaction is %s", hasBeenSatisfied));
		} catch (InterruptedException e) {
			LOG.error("I'm interrupted...", e);
		} finally {
			deleteWorkflow();
			finish();
		}
	}

	private void createWorkflow() {
		JsonObject workflow = new JsonObject();
		workflow.addProperty("name", stringParameter(WORKFLOW_NAME) + "." + instanceId);
		workflow.addProperty("context", stringParameter(CONTEXT_URI));

		workflowUri = post("workflows", workflow.toString()).get("_links").getAsJsonObject().get("self")
				.getAsJsonObject().get("href").getAsString();
	}

	// these parameters are automatically inserted (in the Editor) into a task
	// decomposition when this service is selected from the list
	@Override
	public YParameter[] describeRequiredParams() {
		YParameter[] params = new YParameter[6];
		params[0] = new YParameter(null, YParameter._INPUT_PARAM_TYPE);
		params[0].setDataTypeAndName("string", WORKFLOW_NAME, XSD_NAMESPACE);

		params[1] = new YParameter(null, YParameter._INPUT_PARAM_TYPE);
		params[1].setDataTypeAndName("string", SERVICE_URI, XSD_NAMESPACE);

		params[2] = new YParameter(null, YParameter._INPUT_PARAM_TYPE);
		params[2].setDataTypeAndName("string", CONTEXT_URI, XSD_NAMESPACE);

		params[3] = new YParameter(null, YParameter._INPUT_PARAM_TYPE);
		params[3].setDataTypeAndName("string", GOAL, XSD_NAMESPACE);

		params[4] = new YParameter(null, YParameter._OUTPUT_PARAM_TYPE);
		params[4].setDataTypeAndName("boolean", HAS_BEEN_SATISFIED, XSD_NAMESPACE);

		params[5] = new YParameter(null, YParameter._OUTPUT_PARAM_TYPE);
		params[5].setDataTypeAndName("boolean", HAS_BEEN_FINISHED, XSD_NAMESPACE);
		return params;
	}

	private String stringParameter(String name) {
		return workItemRecord.getDataList().getChild(name).getValue();
	}

	private boolean connected() throws IOException {
		return handle != null && checkConnection(handle);
	}

	private void prepare() {
		httpClient = HttpClients.createDefault();
		sseClient = ClientBuilder.newBuilder().register(SseFeature.class).build();
		serviceUri = stringParameter(SERVICE_URI);
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

	private void createGoals() {
		// we support only one goal
		JsonObject goal = parser.fromJson(stringParameter(GOAL), JsonObject.class);
		goal.addProperty("workflow", fromPath(workflowUri).build().toString());
		String goalUri = post("goals", goal.toString()).get("_links").getAsJsonObject().get("self").getAsJsonObject()
				.get("href").getAsString();

		LOG.info(format("Goal %s created", goalUri));
	}

	private void deleteWorkflow() {
		try {
			httpClient.execute(new HttpDelete(fromPath(workflowUri).build()));
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	private Element getOutputData() {
		Element output = new org.jdom2.Element(workItemRecord.getTaskName());
		Element satisfied = new Element(HAS_BEEN_SATISFIED);
		satisfied.setText(String.valueOf(hasBeenSatisfied));
		Element finished = new Element(HAS_BEEN_FINISHED);
		satisfied.setText(String.valueOf(hasBeenFinished));
		output.addContent(satisfied);
		output.addContent(finished);
		return output;
	}

	private JsonObject post(String path, String json) {
		URI uri = fromPath(serviceUri).path(path).build();
		StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
		HttpPost post = new HttpPost(uri);
		post.setEntity(entity);

		try {
			CloseableHttpResponse response = httpClient.execute(post);
			return new Gson().fromJson(EntityUtils.toString(response.getEntity()), JsonObject.class);
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}
}
