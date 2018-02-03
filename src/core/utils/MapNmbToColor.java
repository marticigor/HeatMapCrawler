package core.utils;

import core.utils.FindColor.RGB;

public class MapNmbToColor<T extends Number> {

	public RGB getRGB(T value) {

		int iValue = value.intValue();
		RGB color = new RGB(50, 50, 50);

		// zelena
		if (iValue > 150 && iValue <= 170) {
			color.red = 0;
			color.green = 255;
			color.blue = 0;
			return color;
		}
		// cervena
		else if (iValue > 210 && iValue <= 230) {
			color.red = 255;
			color.green = 0;
			color.blue = 0;
			return color;
		}

		return color;
	}
}
