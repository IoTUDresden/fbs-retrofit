package io.tudresden.fbs.axis;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.service.Lifecycle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.tudresden.fbs.axis.beans.FeedbackResponse;
import io.tudresden.fbs.axis.beans.Workflow;

/**
 * AXIS2 Wrapper for a fbs instance.
 * 
 * @author andre
 *
 */
public class AXISFeedbackService implements Lifecycle {
	private static final Log LOG = LogFactory.getLog(AXISFeedbackService.class);
	
	private String feedbackServiceHost = "NOT INITIALIZED";
	private String contextUri = "NOT INITIALIZED";
	
	/**
	 * Synchronus call to the fbs instance
	 * 
	 * @return
	 */
	public FeedbackResponse runFeedbackService(String goal, Workflow workflow) {
		LOG.info("starting feedback service for workflow '"+ workflow.getName() + "'");
		long startTime = System.currentTimeMillis();
		
		FeedbackResponse response = new FeedbackResponse();
		response.setHasBeenFinished(true);
		response.setHasBeenSatisfied(true);
		
		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;
		LOG.info(finishedMessage(workflow, response, duration));
		return response;		
	}
	
	public String helloWorld() {
		LOG.info("helloWorld is called");
		return feedbackServiceHost + " " + contextUri;
	}

	public void destroy(ServiceContext arg0) {
		//maybe write back the new host and uri if old values are changed?			
	}

	/**
	 * init is called the first time this service is called
	 */
	public void init(ServiceContext context) throws AxisFault {
		LOG.info("preparing feedback service");
		AxisService service = context.getAxisService();
		feedbackServiceHost = (String)service.getParameterValue("FbsContextId");		
		contextUri = (String)service.getParameterValue("FbsServerAndPort");			
	}
	
	private String finishedMessage(Workflow workflow, FeedbackResponse response, long duration) {
		return "finished feedback service for '"+ workflow.getName() + "' in "+ duration +" ms";
	}

}
