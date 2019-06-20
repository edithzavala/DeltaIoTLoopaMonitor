package api;

import java.util.HashMap;
import java.util.Map;

import org.loopa.comm.message.IMessage;
import org.loopa.comm.message.Message;
import org.loopa.comm.message.MessageType;
import org.loopa.comm.message.PolicyConfigMessageBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Service {
	//private int iterations = 0;

	@PostMapping("/adapt")
	public ResponseEntity<String> createMonDataEntry(@RequestBody String newPolicy) {
		if (newPolicy != null) {
			Application.monOperation = "ok";
			Map<String, String> policyContent = new HashMap<>();
			policyContent.put("adaptation-motes", newPolicy);
			PolicyConfigMessageBody messageContentFL = new PolicyConfigMessageBody(Application.m.getFunctionalLogic().getComponentId(),
					policyContent);
			IMessage mssgAdaptFL = new Message(Application.MONITOR_ID, Application.m.getReceiver().getComponentId(), 2,
					MessageType.REQUEST.toString(), messageContentFL.getMessageBody());
			Application.m.getReceiver().doOperation(mssgAdaptFL);
			return ResponseEntity.status(HttpStatus.CREATED).build();
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}

	@GetMapping("/status")
	public ResponseEntity<String> getStatus() {
		if(Application.monOperation.equals("fail")) {
			//if(iterations > 1) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return ResponseEntity.status(HttpStatus.OK).build();
			//}else {
			//	iterations++;
			//}
		}
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
