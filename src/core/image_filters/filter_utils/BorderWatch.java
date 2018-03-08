package core.image_filters.filter_utils;

public class BorderWatch {

	private int widthFrom;
	private int widthTo;
	private int heightFrom;
	private int heightTo;

	public BorderWatch(int[] values, int border, int xSize, int ySize) {

		if (values.length < 4)
			throw new RuntimeException("values length");

		widthFrom = (values[0] == 0) ? border : values[0];
		widthTo = (values[1] == xSize) ? xSize - border : values[1];
		heightFrom = (values[2] == 0) ? border : values[2];
		heightTo = ((values[3] == ySize) ? ySize - border : values[3]);
	}

	public int getHeightTo() {
		return heightTo;
	}

	public int getHeightFrom() {
		return heightFrom;
	}

	public int getWidthTo() {
		return widthTo;
	}

	public int getWidthFrom() {
		return widthFrom;
	}
}
