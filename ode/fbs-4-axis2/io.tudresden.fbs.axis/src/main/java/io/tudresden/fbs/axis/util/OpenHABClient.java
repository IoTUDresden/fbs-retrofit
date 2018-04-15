package io.tudresden.fbs.axis.util;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface OpenHABClient {
	
	@RequestLine("POST /rest/items/{itemName}")
	@Headers("Content-Type: text/plain")
	void sendCommand(@Param("itemName") String itemName, String command);

}
