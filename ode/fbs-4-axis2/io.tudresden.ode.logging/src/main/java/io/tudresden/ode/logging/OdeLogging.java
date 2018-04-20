package io.tudresden.ode.logging;

import java.util.Properties;

import org.apache.ode.bpel.evt.BpelEvent;
import org.apache.ode.bpel.evt.ProcessEvent;
import org.apache.ode.bpel.evt.ProcessInstanceEvent;
import org.apache.ode.bpel.iapi.BpelEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom Logger for Logging ODE Events
 *
 */
public class OdeLogging implements BpelEventListener {
	private static final Logger LOG = LoggerFactory.getLogger(OdeLogging.class);
	
	@Override
	public void onEvent(BpelEvent event) {
		if(event instanceof ProcessEvent)	
			handleProcessEvent((ProcessEvent)event);
		else if (event instanceof ProcessInstanceEvent)
			handleProcessInstanceEvent((ProcessInstanceEvent)event);	
	}

	public void shutdown() {
		LOG.info("[io.tudresden] shutting down engine...");
		
	}

	public void startup(Properties arg0) {
		LOG.info("[io.tudresden] starting ode engine...");		
	}
	
	private void handleProcessEvent(ProcessEvent event) {
		LOG.info(String.format("%s for process with name: %s",
				BpelEvent.eventName(event), 				
				event.getProcessName().toString()));
	}
	
	private void handleProcessInstanceEvent(ProcessInstanceEvent event) {
		LOG.info(String.format("%s for process with name: %s", 
				BpelEvent.eventName(event), 
				event.getProcessName().toString()));		
	}
}
