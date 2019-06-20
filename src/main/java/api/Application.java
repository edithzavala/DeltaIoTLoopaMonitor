package api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.loopa.comm.message.IMessage;
import org.loopa.comm.message.LoopAElementMessageCode;
import org.loopa.comm.message.Message;
import org.loopa.comm.message.MessageType;
import org.loopa.comm.message.PolicyConfigMessageBody;
import org.loopa.element.functionallogic.enactor.IFunctionalLogicEnactor;
import org.loopa.element.functionallogic.enactor.monitor.MonitorFunctionalLogicEnactor;
import org.loopa.element.sender.messagesender.IMessageSender;
import org.loopa.monitor.IMonitor;
import org.loopa.monitor.Monitor;
import org.loopa.policy.IPolicy;
import org.loopa.policy.Policy;
import org.loopa.recipient.Recipient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fasterxml.jackson.databind.ObjectMapper;

import logic.FunctionalLogicEnactorManager;
import logic.MonitorMessageSender;
import model.DeltaIoTMonitorSenderPolicy;

@SpringBootApplication
public class Application {
	public static String MONITOR_ID;
	public static IMonitor m;
	public static String monOperation;

	// create Monitor with new FLE(manager), new Policy and new Sender.
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		MONITOR_ID = args[0];
		String policyFilePath = "/tmp/config/" + args[1];
		monOperation = args[2];
		
		/** Init policy (MANDATORY) **/
		Map<String, String> initPolicy = new HashMap<>();
		initPolicy.put(LoopAElementMessageCode.MSSGINFL.toString(), "1");
		initPolicy.put(LoopAElementMessageCode.MSSGINAL.toString(), "2");
		initPolicy.put(LoopAElementMessageCode.MSSGADAPT.toString(), "3");
		initPolicy.put(LoopAElementMessageCode.MSSGOUTFL.toString(), "4");
		initPolicy.put(LoopAElementMessageCode.MSSGOUTAL.toString(), "5");
		
		/****** Create monitor ***/
		// System.out.println(policyContent.toString());
		IMessageSender sMS = new MonitorMessageSender();
		IFunctionalLogicEnactor flE = new MonitorFunctionalLogicEnactor(new FunctionalLogicEnactorManager());
		IPolicy mp = new Policy(MONITOR_ID, initPolicy);
		m = new Monitor(MONITOR_ID, mp, flE, sMS);
		m.construct();
		
		/***** Add logic policies ****/
		String policyString = "";
		Map<String, String> policyContent = new HashMap<>();
		try {
			policyString = new String(Files.readAllBytes(Paths.get(policyFilePath)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		policyContent.put("config", policyString);
		PolicyConfigMessageBody messageContentFL = new PolicyConfigMessageBody(m.getFunctionalLogic().getComponentId(),
				policyContent);
		IMessage mssgAdaptFL = new Message(MONITOR_ID, m.getReceiver().getComponentId(), 2,
				MessageType.REQUEST.toString(), messageContentFL.getMessageBody());
		m.getReceiver().doOperation(mssgAdaptFL);
		
		/*** Add recipients and corresponding policies ****/
		DeltaIoTMonitorSenderPolicy monitorRecepients;
		ObjectMapper mapper = new ObjectMapper();
		try {
			monitorRecepients = mapper.readValue(policyString, DeltaIoTMonitorSenderPolicy.class);
			monitorRecepients.getRecipients().forEach(recepient -> {
				m.addElementRecipient(
						new Recipient(recepient.getId(), recepient.getTypeOfData(), recepient.getRecipient()));
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*********** Start monitoring ***********/
		try {
			Thread.sleep(1000);
			((FunctionalLogicEnactorManager) ((IFunctionalLogicEnactor) m.getFunctionalLogic().getMessageManager())
					.getManager()).start();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
