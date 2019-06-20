package client;

import org.loopa.comm.message.IMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class AnalyzerClient {
	protected final Logger LOGGER = LoggerFactory.getLogger(getClass().getName());
	
	public void requestAnalysis(String url, IMessage mssg) {
		LOGGER.info("Post monitoring data to analysis");
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.postForEntity(url, mssg.getMessageBody(),String.class);
		LOGGER.info("Receive response (" + response.getStatusCode()+")");
//		LOGGER.info("Send mssg: " + mssg.getMessageBody().toString() + " to: " + url);
	}
}
