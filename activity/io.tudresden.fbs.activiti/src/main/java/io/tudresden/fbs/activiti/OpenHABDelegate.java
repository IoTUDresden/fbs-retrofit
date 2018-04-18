package io.tudresden.fbs.activiti;

import java.io.IOException;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.tudresden.fbs.activiti.util.OpenHABClient;

/**
 * Delegate for OpenHAB
 *
 */
public class OpenHABDelegate implements JavaDelegate {
	private static final Logger LOG = LoggerFactory.getLogger(OpenHABDelegate.class);
	
	protected static final String commandLog = "sending command '%s' to '%s/rest/items/%s'...";
	protected static final String commandLogDone = "sending command '%s' to '%s/rest/items/%s'...Done";

	private static final String host = "localhost:8080";
	private static final String itemName = "Light_GF_Corridor_Ceiling";
	private static final String command = "ON";

	@Override
	public void execute(DelegateExecution args) {
		LOG.info(String.format(commandLog, command, host, itemName));

		OpenHABClient client = new OpenHABClient(host);
		client.postCommand(itemName, command);
		try {
			client.close();
		} catch (IOException e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		LOG.info(String.format(commandLogDone, command, host, itemName));
	}
}
