package logic;

import org.loopa.comm.message.IMessage;
import org.loopa.element.sender.messagesender.AMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.AnalyzerClient;
import client.DeltaIoTClient;

public class MonitorMessageSender extends AMessageSender {
	protected final Logger LOGGER = LoggerFactory.getLogger(getClass().getName());
	private DeltaIoTClient me = new DeltaIoTClient();
	private AnalyzerClient analyzer = new AnalyzerClient();

	@Override
	public void processMessage(IMessage mssg) {
		switch (mssg.getMessageBody().get("type")) {
		case "ME_IoTNetwork":
			me.requestMotesData((String) this.getComponent().getComponentRecipient("IoTNetwork").getRecipient(), this.getComponent().getElement(), mssg);
			break;
		case "ANALYZE_Analyzer":
			analyzer.requestAnalysis((String) this.getComponent().getComponentRecipient("Analyzer").getRecipient(),
					mssg);
			break;
		default:
			break;
		}
	}
}
