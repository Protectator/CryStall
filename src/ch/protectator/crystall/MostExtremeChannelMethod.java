package ch.protectator.crystall;
import java.awt.image.BufferedImage;

import net.sourceforge.jiu.gui.awt.BufferedRGB24Image;

public class MostExtremeChannelMethod implements BlendMethod {
	
	private int[][][] max;
	private int[][][] min;
	private float[][][] avg;
	private int channels;
	private int amount;
	private int width;
	private int height;
	private int type;

	public MostExtremeChannelMethod(int nbChannels, int width, int height, int type) {
		this.channels = nbChannels;
		this.width = width;
		this.height = height;
		this.type = type;
		this.amount = 0;
		this.max = new int[width][][];
		this.min = new int[width][][];
		this.avg = new float[width][][];
		int x, y, channel;
		for (x = 0; x < width; x++) {
			this.max[x] = new int[height][];
			this.min[x] = new int[height][];
			this.avg[x] = new float[height][];
			for (y = 0; y < height; y++) {
				this.max[x][y] = new int[channels];
				this.min[x][y] = new int[channels];
				this.avg[x][y] = new float[channels];
				for (channel = 0; channel < channels; channel++) {
					this.max[x][y][channel] = 0;
					this.min[x][y][channel] = 255;
					this.avg[x][y][channel] = 0;
				}
			}
		}
	}

	@Override
	public void iterate(BufferedRGB24Image newFrame) {
		int x, y, channel;
		int[][] columnMax, columnMin;
		float[][] columnAvg;
		int[] pixelMax, pixelMin;
		float[] pixelAvg;
		for (x = 0; x < width; x++) {
			columnMax = max[x];
			columnMin = min[x];
			columnAvg = avg[x];
			for (y = 0; y < height; y++) {
				pixelMax = columnMax[y];
				pixelMin = columnMin[y];
				pixelAvg = columnAvg[y];
				for (channel = 0; channel < channels; channel++) {
					max[x][y][channel] = maxIterate(pixelMax[channel], newFrame.getSample(channel, x, y));
					min[x][y][channel] = minIterate(pixelMin[channel], newFrame.getSample(channel, x, y));
					avg[x][y][channel] = averageIterate(pixelAvg[channel], amount, newFrame.getSample(channel, x, y));
				}
			}
		}
		amount++;
	}

	@Override
	public void finish() {
		return;
	}

	@Override
	public String getName() {
		return "extremeChannels";
	}
	
	@Override
	public BufferedRGB24Image getPixels() {
		BufferedRGB24Image result = new BufferedRGB24Image(new BufferedImage(width, height, type));
		int x, y, channel, high, low;
		int[][] columnMax, columnMin;
		float[][] columnAvg;
		int[] pixelMax, pixelMin;
		float[] pixelAvg;
		for (x = 0; x < width; x++) {
			columnMax = max[x];
			columnMin = min[x];
			columnAvg = avg[x];
			for (y = 0; y < height; y++) {
				pixelMax = columnMax[y];
				pixelMin = columnMin[y];
				pixelAvg = columnAvg[y];
				for (channel = 0; channel < channels; channel++) {
					high = pixelMax[channel] - (int)pixelAvg[channel];
					low = (int)pixelAvg[channel] - pixelMin[channel];
					if (high > low) {
						result.putSample(channel, x, y, pixelMax[channel]);
					} else {
						result.putSample(channel, x, y, pixelMin[channel]);
					}
				}
			}
		}
		return result;
	}
	
	private int maxIterate(int lastWeight, int newValue) {
		return Math.max(lastWeight, newValue);
	}
	
	private int minIterate(int lastWeight, int newValue) {
		return Math.min(lastWeight, newValue);
	}

	private float averageIterate(float average, int lastWeight, int newValue) {
		return (average * (float)lastWeight + (float)newValue) / (float)(lastWeight + 1);
	}
	
}
