package io.tudresden.fbs.axis;

import io.tudresden.fbs.axis.beans.FeedbackResponse;

/**
 * Service impl for debugging purposes.
 * 
 * if workflow name contains 'fail', 'hasBeensatisfied' in response will return 'false'
 * 
 * @author andre
 *
 */
public class AXISFeedbackServiceDebug extends AXISFeedbackService{
	protected static final String finishedLog = "finished FeedbackService.\n    Satisfied = %s";
	
	@Override
	public FeedbackResponse runFeedbackService(String goal, String workflowName) {
		LOG.info("starting FeedbackService");
		LOG.info("Goal: " + goal);
		LOG.info("Workflow Name: " + workflowName);
		
		FeedbackResponse response = new FeedbackResponse();
		response.setHasBeenFinished(true);
		response.setHasBeenSatisfied(!workflowName.contains("fail"));		
		
		LOG.info(String.format(finishedLog, String.valueOf(response.isHasBeenSatisfied())));			
		return response;
	}

}
