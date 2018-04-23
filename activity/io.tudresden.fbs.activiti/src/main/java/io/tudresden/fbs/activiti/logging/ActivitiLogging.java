package io.tudresden.fbs.activiti.logging;

import org.activiti.engine.delegate.event.ActivitiActivityEvent;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActivitiLogging implements ActivitiEventListener {
	private static final Logger LOG = LoggerFactory.getLogger(ActivitiLogging.class);


	@Override
	public boolean isFailOnException() {
		return false;
	}

	@Override
	public void onEvent(ActivitiEvent event) {
		StringBuilder builder = new StringBuilder();
		builder.append("\nProcess Id:    ").append(event.getProcessInstanceId());
		
		if(event instanceof ActivitiActivityEvent)
			builder.append("\nActiviti Name: ").append(((ActivitiActivityEvent)event).getActivityName());	
		
		builder.append("\nDefinition Id: ").append(event.getProcessDefinitionId());
		builder.append("\nEvent:         ").append(event.getType());
		
		LOG.info(builder.toString());			

	}

}
