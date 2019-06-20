package client;

import java.util.HashMap;
import java.util.Map;

import org.loopa.comm.message.IMessage;
import org.loopa.comm.message.LoopAElementMessageCode;
import org.loopa.comm.message.Message;
import org.loopa.comm.message.MessageType;
import org.loopa.generic.element.ILoopAElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class DeltaIoTClient {
	protected final Logger LOGGER = LoggerFactory.getLogger(getClass().getName());

	public void requestMotesData(String url, ILoopAElement receiverResponse, IMessage mssg) {
		String ids = mssg.getMessageBody().get("content");
		LOGGER.info("Request monitoring data");
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate
				.getForEntity(url + "?id=" + ids.substring(1, ids.length() - 1).trim(), String.class);
		LOGGER.info("Receive monitoring data (" + response.getStatusCode() + ")");
		// LOGGER.info("Motes: " + response.getBody());
		Map<String, String> messageBody = new HashMap<>();
		messageBody.put("moteInfo", response.getBody());
		IMessage monDataMssg = new Message("ME", receiverResponse.getElementId(),
				Integer.parseInt(receiverResponse.getElementPolicy().getPolicyContent()
						.get(LoopAElementMessageCode.MSSGINFL.toString())),
				MessageType.RESPONSE.toString(), messageBody);
		receiverResponse.getReceiver().doOperation(monDataMssg);
	}
}
