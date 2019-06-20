package model;

import java.util.List;

import org.loopa.recipient.IRecipient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeltaIoTRecepient implements IRecipient {

	private String id;

	private List<String> typeOfData;

	private String recipient;
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setTypeOfData(List<String> typeOfData) {
		this.typeOfData = typeOfData;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	@Override
	public String getRecipient() {
		return this.recipient;
	}

	@Override
	public String getrecipientId() {
		return this.id;
	}

	@Override
	public List<String> getTypeOfData() {
		return this.typeOfData;
	}
}
