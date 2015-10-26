package gr.bytewise.jrhythm.guitk;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

public class IconedLabel extends JLabel {

	protected List<GResourceLabel> labels;
	protected Dimension dimension;

	protected IconedLabel(Builder builder) {
		super();
		labels = builder.labels;
		dimension = builder.dimension;
	}

	public static class Builder {

		protected List<GResourceLabel> labels;
		protected Dimension dimension;

		public Builder() {
			this.labels = new ArrayList<GResourceLabel>();
		}

		public Builder add(String label, String resource) {
			labels.add(new GResourceLabel(label, resource));
			return this;
		}

		public Builder setDimension(Dimension dimension) {
			this.dimension = dimension;
			return this;
		}

		public Builder setDimension(int x, int y) {
			this.dimension = new Dimension(x, y);
			return this;
		}

		public IconedLabel build() {
			return new IconedLabel(this);
		}
	}

	public void setStatus(String title) {
		for (GResourceLabel label : labels) {
			if (label.getTitle().equalsIgnoreCase(title)) {
				setIcon(UI.getScaledIcon(dimension == null ? 24 : dimension.width, dimension == null ? 24 : dimension.height, label.resource));
			}
		}
	}
}
