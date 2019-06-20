package model;

import java.util.Map;

import deltaiot.services.Mote;

public class MotesInfo {
	private int run;
	private Map<String, Mote> motes;

	public int getRun() {
		return run;
	}

	public void setRun(int run) {
		this.run = run;
	}

	public Map<String, Mote> getMotes() {
		return motes;
	}

	public void setMotes(Map<String, Mote> motes) {
		this.motes = motes;
	}

}
