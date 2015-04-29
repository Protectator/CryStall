
public class MaxMethod implements BlendMethod {

	@Override
	public int[] initialize() {
		return new int[]{0, 0, 0};
	}

	@Override
	public int[] iterate(int step, int[] lastIteration, int[] newPixel) {
		for (int channel = 0; channel < lastIteration.length; channel++) {
			lastIteration[channel] = Math.max(lastIteration[channel], newPixel[channel]);
		}
		return lastIteration;
	}

	@Override
	public int[] finish(int totalSteps, int[] finalIteration) {
		return finalIteration;
	}

	@Override
	public String getName() {
		return "max";
	}
}
