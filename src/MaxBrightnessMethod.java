
public class MaxBrightnessMethod implements BlendMethod {

	@Override
	public int[] initialize() {
		return new int[]{0, 0, 0};
	}

	@Override
	public int[] iterate(int step, int[] lastIteration, int[] newPixel) {
		if (brightness(newPixel) >= brightness(lastIteration)) {
			return newPixel;
		}
		return lastIteration;
	}

	@Override
	public int[] finish(int totalSteps, int[] finalIteration) {
		return finalIteration;
	}

	@Override
	public String getName() {
		return "maxBrightness";
	}
	
	public int brightness(int[] channels) {
		int brightness = 0;
		for (int value : channels) {
			brightness += value;
		}
		return brightness/channels.length;
	}
}
