package model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeltaIoTMonitorFLPolicy {
	private int monFreq;
	private List<String> motes;

	public int getMonFreq() {
		return monFreq;
	}

	public void setMonFreq(int monFreq) {
		this.monFreq = monFreq;
	}

	public List<String> getMotes() {
		return motes;
	}

	public void setMotes(List<String> motes) {
		this.motes = motes;
	}

}
