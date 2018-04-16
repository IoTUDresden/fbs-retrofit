package io.tudresden.fbs.axis;

import java.util.UUID;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.service.Lifecycle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.tudresden.fbs.axis.beans.FeedbackResponse;
import io.tudresden.fbs.axis.util.Compensation;

/**
 * AXIS2 Wrapper for a fbs instance.
 * 
 * @author andre
 *
 */
public class AXISFeedbackService implements Lifecycle {
	protected static final Log LOG = LogFactory.getLog(AXISFeedbackService.class);
	
	protected String feedbackServiceHost = "NOT INITIALIZED";
	protected String contextUri = "NOT INITIALIZED";

	
	public FeedbackResponse runFeedbackService(String goal, String workflowName) {
		LOG.info("starting feedback service for workflow '"+ workflowName + "'");
		long startTime = System.currentTimeMillis();
		
		Compensation compensation = new Compensation(feedbackServiceHost, contextUri);
		compensation.setGoal(goal);
		compensation.setWorkflowName(workflowName + "." + UUID.randomUUID().toString());
		FeedbackResponse response = compensation.work();
		
		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;
		LOG.info(finishedMessage(workflowName, response, duration));
		return response;		
	}
	
	public String showConfig() {
		LOG.info("showConfig() called");
		return "Host: " + feedbackServiceHost + " Context: " + contextUri;
	}

	@Override
	public void destroy(ServiceContext arg0) {
		//maybe write back the new host and uri if old values are changed?			
	}

	/**
	 * init is called the first time this service is called
	 */
	@Override
	public void init(ServiceContext context) throws AxisFault {
		LOG.info("preparing feedback service...");
		AxisService service = context.getAxisService();
		feedbackServiceHost = (String)service.getParameterValue("FbsContextId");		
		contextUri = (String)service.getParameterValue("FbsServerAndPort");			
	}
	
	private String finishedMessage(String workflowName, FeedbackResponse response, long duration) {
		return "finished feedback service for '"+ workflowName + "' in "+ duration +" ms";
	}

}
