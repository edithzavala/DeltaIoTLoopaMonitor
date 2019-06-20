package logic;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.loopa.comm.message.IMessage;
import org.loopa.comm.message.LoopAElementMessageBody;
import org.loopa.comm.message.LoopAElementMessageCode;
import org.loopa.comm.message.Message;
import org.loopa.comm.message.MessageType;
import org.loopa.element.functionallogic.enactor.monitor.IMonitorFleManager;
import org.loopa.generic.element.component.ILoopAElementComponent;
import org.loopa.policy.IPolicy;
import org.loopa.policy.Policy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;

import api.Application;
import model.DeltaIoTMonitorFLPolicy;
import model.MotesInfo;

public class FunctionalLogicEnactorManager implements IMonitorFleManager {
	protected final Logger LOGGER = LoggerFactory.getLogger(getClass().getName());
	private final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(2);
	private IPolicy allPolicy = new Policy(this.getClass().getName(), new HashMap<String, String>());
	// private int iterations = 0;
	private ScheduledFuture<?> futureTask;
	private ILoopAElementComponent owner;
	private DeltaIoTMonitorFLPolicy policy;

	@Override
	public ILoopAElementComponent getComponent() {
		return this.owner;
	}

	@Override
	public void setComponent(ILoopAElementComponent c) {
		this.owner = c;

	}

	@Override
	public void processLogicData(Map<String, String> moteInfo) {
		LOGGER.info("Send data to analysis");
		sendDataToAnalysis(moteInfo.get("moteInfo"));
		ObjectMapper mapper = new ObjectMapper();
		try {
			MotesInfo mInfo = mapper.readValue(moteInfo.get("moteInfo"), MotesInfo.class);
			if (mInfo.getRun() == 94) {
				if (futureTask != null) {
					futureTask.cancel(true);
					LOGGER.info("Stop monitoring (simulation)");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendDataToAnalysis(String moteInfo) {
		LoopAElementMessageBody messageContent = new LoopAElementMessageBody("ANALYZE", moteInfo);
		messageContent.getMessageBody().put("contentType", "monitoringData");
		String code = this.getComponent().getElement().getElementPolicy().getPolicyContent()
				.get(LoopAElementMessageCode.MSSGOUTFL.toString());
		IMessage mssg = new Message(this.owner.getComponentId(), this.allPolicy.getPolicyContent().get(code),
				Integer.parseInt(code), MessageType.REQUEST.toString(), messageContent.getMessageBody());
		((ILoopAElementComponent) this.owner.getComponentRecipient(mssg.getMessageTo()).getRecipient())
				.doOperation(mssg);
	}

	@Override
	public void setConfiguration(Map<String, String> config) {
		LOGGER.info("Component (re)configured");
		if (config.containsKey("adaptation-motes")) {
			// LOGGER.info("New policy: " + config.get("adaptation-motes"));
			ObjectMapper mapper = new ObjectMapper();
			try {
				DeltaIoTMonitorFLPolicy newPolicy = mapper.readValue(config.get("adaptation-motes"),
						DeltaIoTMonitorFLPolicy.class);
				this.policy.getMotes().clear();
				this.policy.setMotes(newPolicy.getMotes());
				LOGGER.info("Monitor adapted, new motes: " + this.policy.getMotes());
				restart();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (config.containsKey("adaptation-freq")) {
//			this.policy.setMonFreq(Integer.parseInt(config.get("adaptation-freq")));
//			restart();
		} else if (config.containsKey("config")) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				this.policy = mapper.readValue(config.get("config"), DeltaIoTMonitorFLPolicy.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.allPolicy.update(new Policy(this.allPolicy.getPolicyOwner(), config));
	}

	private void restart() {
		if (futureTask != null) {
			futureTask.cancel(true);
		}
		start();
	}

	private Runnable getMonitoringTask() {
		Runnable monitoringTask = new Runnable() {
			@Override
			public void run() {
				// helps to update motes if necessary
				LoopAElementMessageBody messageContent = new LoopAElementMessageBody("ME",
						policy.getMotes().toString());
				// LOGGER.info("Request info motes: " + policy.getMotes());
				String code = getComponent().getElement().getElementPolicy().getPolicyContent()
						.get(LoopAElementMessageCode.MSSGOUTFL.toString());
				IMessage mssg = new Message(owner.getComponentId(), allPolicy.getPolicyContent().get(code),
						Integer.parseInt(code), MessageType.REQUEST.toString(), messageContent.getMessageBody());
				((ILoopAElementComponent) owner.getComponentRecipient(mssg.getMessageTo()).getRecipient())
						.doOperation(mssg);
				//LOGGER.info(getComponent().getElement().getElementId() + " Send message to sender");
			}
		};
		return monitoringTask;
	}

	public void start() {
		// helps to update frequency if necessary
		if (Application.monOperation.equals("fail")) {
			// if (iterations > 2) {
			// try {
			// Thread.sleep(60000);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
			// } else {
			// iterations++;
			// }
			this.futureTask = SCHEDULER.scheduleAtFixedRate(getMonitoringTask(), 0, 60000, TimeUnit.MILLISECONDS);
		} else {
			this.futureTask = SCHEDULER.scheduleAtFixedRate(getMonitoringTask(), 0, this.policy.getMonFreq(),
					TimeUnit.MILLISECONDS);
		}
	}
}
