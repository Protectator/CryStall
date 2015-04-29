
public class MinMethod implements BlendMethod {

	@Override
	public int[] initialize() {
		return new int[]{255, 255, 255};
	}

	@Override
	public int[] iterate(int step, int[] lastIteration, int[] newPixel) {
		for (int channel = 0; channel < lastIteration.length; channel++) {
			lastIteration[channel] = Math.min(lastIteration[channel], newPixel[channel]);
		}
		return lastIteration;
	}

	@Override
	public int[] finish(int totalSteps, int[] finalIteration) {
		return finalIteration;
	}

}
