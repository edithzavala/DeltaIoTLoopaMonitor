package model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeltaIoTMonitorSenderPolicy {
	private List<DeltaIoTRecepient> recipients;

	public List<DeltaIoTRecepient> getRecipients() {
		return recipients;
	}

	public void setRecipients(List<DeltaIoTRecepient> recipients) {
		this.recipients = recipients;
	}

}
