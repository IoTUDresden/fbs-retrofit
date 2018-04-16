package io.tudresden.fbs.axis;

/**
 * for debug purposes. Just logs the calls to OpenHAB
 * 
 * @author andre
 *
 */
public class AXISOpenHABServiceDebug extends AXISOpenHABService {
	
	
	@Override
	public void postCommand(String itemName, String command) {
		LOG.info(String.format(commandLog, command, openhabHost, itemName));				
	}

}
