package gr.bytewise.jrhythm.guitk;

public class GResourceLabel {

	public String title;

	public GResourceLabel(String title, String resource) {
		this.title = title;
		this.resource = resource;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String resource;

}
