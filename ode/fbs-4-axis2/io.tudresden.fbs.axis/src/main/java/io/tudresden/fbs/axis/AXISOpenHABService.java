package io.tudresden.fbs.axis;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.service.Lifecycle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.tudresden.fbs.axis.util.OpenHABClient;

public class AXISOpenHABService implements Lifecycle {
	protected static final Log LOG = LogFactory.getLog(AXISOpenHABService.class);
	protected static final String commandLog = "sending command '%s' to '%s/rest/items/%s'...";
	
	protected String openhabHost = "ERROR";

	
	private OpenHABClient client;

	/**
	 * Post a command to OpenHAB
	 * 
	 * @param itemName
	 *            name of the item
	 * @param command
	 *            e.g. 'ON'
	 */
	public void postCommand(String itemName, String command) {
		LOG.info(String.format(commandLog, command, openhabHost, itemName));
		client.postCommand(itemName, command);
	}

	public void destroy(ServiceContext context) {
		// nothing to do here		
	}

	/**
	 * init is called the first time this service is called
	 */
	public void init(ServiceContext context) throws AxisFault {
		LOG.info("preparing OpenHABService...");
		AxisService service = context.getAxisService();
		openhabHost = (String)service.getParameterValue("OpenHABHost");		
		
		if(!openhabHost.startsWith("http"))
			openhabHost = "http://" + openhabHost;
		
		client = new OpenHABClient(openhabHost);
	}

}
