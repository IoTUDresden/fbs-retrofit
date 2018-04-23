package io.tudresden.yawl.openhabservice;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawl.elements.data.YParameter;
import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;
import org.yawlfoundation.yawl.engine.interfce.interfaceB.InterfaceBWebsideController;

/**
 * OpenhabService
 *
 */
public class OpenhabService extends InterfaceBWebsideController {
	private static final Logger LOG = LoggerFactory.getLogger(OpenhabService.class);
	private static final String PWD = "1234";
	private static final String USER = "openhabService";
	
	private static final String OH_HOST = "OpenhabHost";
	private static final String ITEM_NAME = "ItemName";
	private static final String CMD_NAME = "CommandName";
	
	private String handle;
	private WorkItemRecord workItemRecord;

	@Override
	public void handleCancelledWorkItemEvent(WorkItemRecord wir) {
		LOG.info("################ canceled ###################");		
	}

	@Override
	public void handleEnabledWorkItemEvent(WorkItemRecord wir) {
		LOG.info("################ handleEnabledWorkItemEvent ###################");
		
		try {

			// connect only if not already connected
			if (!connected())
				handle = connect(USER, PWD);
			// handle = connect(engineLogonName, engineLogonPassword);

			// checkout ... process ... checkin
			workItemRecord = checkOut(wir.getID(), handle);
			wir = workItemRecord;

			work();

			// somewhere in the docs it says, it is possible that the session is
			// expired, if the service takes too long
			// so we just get a new session
			if (!connected())
				handle = connect(USER, PWD);
			checkInWorkItem(wir.getID(), wir.getDataList(), null, null, handle);

		} catch (Exception ioe) {
			ioe.printStackTrace();
		}		
	}
	
	@Override
	public YParameter[] describeRequiredParams() {
		YParameter[] params = new YParameter[3];
		params[0] = new YParameter(null, YParameter._INPUT_PARAM_TYPE);
		params[0].setDataTypeAndName("string", OH_HOST, XSD_NAMESPACE);

		params[1] = new YParameter(null, YParameter._INPUT_PARAM_TYPE);
		params[1].setDataTypeAndName("string", ITEM_NAME, XSD_NAMESPACE);
		
		params[2] = new YParameter(null, YParameter._INPUT_PARAM_TYPE);
		params[2].setDataTypeAndName("string", CMD_NAME, XSD_NAMESPACE);
		return params;
	}
	
	private void work() {
		String cmd = stringParameter(CMD_NAME);
		String host = stringParameter(OH_HOST);
		String item = stringParameter(ITEM_NAME);
		
		LOG.info("send command '{}' to {}/rest/items/{} ...", cmd, host, item);
		
		OpenHABClient client = new OpenHABClient(host);
		client.postCommand(item, cmd);
		
		LOG.info("send command '{}' to {}/rest/items/{} ...done", cmd, host, item);
		
		try {
			client.close();
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}		
	}
	
	private String stringParameter(String name) {
		return workItemRecord.getDataList().getChild(name).getValue();
	}
	
	private boolean connected() throws IOException {
		return handle != null && checkConnection(handle);
	}

}
