package core.image_filters.filter_utils;

public class ChunksOrWhole {

	public static int[] decide(int[] args, boolean wholePicture, int imageWidth, int imageHeight) {
		if (args.length < 4)
			throw new RuntimeException("Arg length");
		int[] values = new int[4];
		values[0] = (wholePicture) ? 0 : args[0];
		values[1] = (wholePicture) ? imageWidth : args[1];
		values[2] = (wholePicture) ? 0 : args[2];
		values[3] = (wholePicture) ? imageHeight : args[3];
		return values;
	}
}
