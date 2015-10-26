package gr.bytewise.jrhythm.gui.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "MidiConfig")
@XmlAccessorType(XmlAccessType.FIELD)
public class MidiConfig {

	private String deviceName;

	private Integer midiIndex;

	private Integer instrumentIndex;

	protected MidiConfig() {
	}

	public MidiConfig(String deviceName, int index, int instrumentIndex) {
		setDeviceName(deviceName);
		setDeviceIndex(index);
		setInstrumentIndex(instrumentIndex);
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public Integer getDeviceIndex() {
		return midiIndex;
	}

	public void setDeviceIndex(Integer index) {
		this.midiIndex = index;
	}

	public Integer getInstrumentIndex() {
		return instrumentIndex;
	}

	public void setInstrumentIndex(Integer instrumentIndex) {
		this.instrumentIndex = instrumentIndex;
	}

}