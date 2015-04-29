
public interface BlendMethod {
	public int[] initialize();
	public int[] iterate(int step, int[] lastIteration, int[] newPixel);
	public int[] finish(int totalSteps, int[] finalIteration);
	public String getName();
}
