package ch.protectator.crystall;
import java.awt.image.BufferedImage;

import net.sourceforge.jiu.gui.awt.BufferedRGB24Image;

public class AverageMethod implements BlendMethod {
	
	private float[][][] average;
	private int amount;
	private int channels;
	private int width;
	private int height;
	private int type;

	public AverageMethod(int nbChannels, int width, int height, int type) {
		this.channels = nbChannels;
		this.width = width;
		this.height = height;
		this.type = type;
		this.average = new float[width][][];
		this.amount = 0;
		int x, y, channel;
		for (x = 0; x < width; x++) {
			this.average[x] = new float[height][];
			for (y = 0; y < height; y++) {
				this.average[x][y] = new float[channels];
				for (channel = 0; channel < channels; channel++) {
					this.average[x][y][channel] = 0;	
				}
			}
		}
	}

	@Override
	public void iterate(BufferedRGB24Image newFrame) {
		int x, y, channel;
		float[][] column;
		float[] pixel;
		for (x = 0; x < width; x++) {
			column = average[x];
			for (y = 0; y < height; y++) {
				pixel = column[y];
				for (channel = 0; channel < channels; channel++) {
					average[x][y][channel] = averageIterate(pixel[channel], amount, newFrame.getSample(channel, x, y));
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
		return "average";
	}
	
	@Override
	public BufferedRGB24Image getPixels() {
		BufferedRGB24Image result = new BufferedRGB24Image(new BufferedImage(width, height, type));
		int x, y, channel;
		for (x = 0; x < width; x++) {
			for (y = 0; y < height; y++) {
				for (channel = 0; channel < channels; channel++) {
					result.putSample(channel, x, y, (int)average[x][y][channel]);
				}
			}
		}
		return result;
	}
	
	private float averageIterate(float average, int lastWeight, int newValue) {
		return (average * (float)lastWeight + (float)newValue) / (float)(lastWeight + 1);
	}

}
