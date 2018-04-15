package io.tudresden.fbs.axis;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.service.Lifecycle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import io.tudresden.fbs.axis.util.OpenHABClient;

public class AXISOpenHABService implements Lifecycle {
	private static final Log LOG = LogFactory.getLog(AXISOpenHABService.class);
	
	private String openhabHost = "ERROR";
	private String commandLog = "sending command '%s' to '%s/rest/items/%s'...";
	
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
		client.sendCommand(itemName, command);
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
		
		client = Feign.builder()
				.encoder(new JacksonEncoder())
				.decoder(new JacksonDecoder())
				.target(OpenHABClient.class, openhabHost);
	}

}
