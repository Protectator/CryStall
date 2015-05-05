package ch.protectator.crystall;
import java.awt.image.BufferedImage;

import net.sourceforge.jiu.gui.awt.BufferedRGB24Image;

public class MaxBrightnessMethod implements BlendMethod {
	
	private int[][][] maxB;
	private int channels;
	private int width;
	private int height;
	private int type;

	public MaxBrightnessMethod(int nbChannels, int width, int height, int type) {
		this.channels = nbChannels;
		this.width = width;
		this.height = height;
		this.type = type;
		this.maxB = new int[width][][];
		int x, y, channel;
		for (x = 0; x < width; x++) {
			this.maxB[x] = new int[height][];
			for (y = 0; y < height; y++) {
				this.maxB[x][y] = new int[channels];
				for (channel = 0; channel < channels; channel++) {
					this.maxB[x][y][channel] = 0;	
				}
			}
		}
	}

	@Override
	public void iterate(BufferedRGB24Image newFrame) {
		int x, y, channel;
		int[][] column;
		int[] pixel, newPixel;
		for (x = 0; x < width; x++) {
			column = maxB[x];
			for (y = 0; y < height; y++) {
				pixel = column[y];
				newPixel = new int[channels];
				for (channel = 0; channel < channels; channel++) {
					newPixel[channel] = newFrame.getSample(channel, x, y);
				}
				maxB[x][y] = maxBrightIterate(pixel, newPixel);
			}
		}
	}

	@Override
	public void finish() {
		return;
	}

	@Override
	public String getName() {
		return "maxBrightness";
	}
	
	@Override
	public BufferedRGB24Image getPixels() {
		BufferedRGB24Image result = new BufferedRGB24Image(new BufferedImage(width, height, type));
		int x, y, channel;
		for (x = 0; x < width; x++) {
			for (y = 0; y < height; y++) {
				for (channel = 0; channel < channels; channel++) {
					result.putSample(channel, x, y, maxB[x][y][channel]);
				}
			}
		}
		return result;
	}
	
	private int[] maxBrightIterate(int[] lastWeight, int[] newValue) {
		if (brightness(newValue) >= brightness(lastWeight)) {
			return newValue;
		}
		return lastWeight;
	}
	
	private int brightness(int[] channels) {
		int brightness = 0;
		for (int value : channels) {
			brightness += value;
		}
		return brightness/channels.length;
	}

}