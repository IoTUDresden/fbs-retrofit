package io.tudresden.fbs.axis.util;

public class OpenHABClient extends AbstractHttpClient {
	private static final String itemPath = "/rest/items/";
	
	public OpenHABClient(String openHabHost) {
		super(openHabHost);	
	}
	
	public void postCommand(String itemName, String command) {
		postTextPlain(itemPath + itemName, command);
	}
	
	

}
