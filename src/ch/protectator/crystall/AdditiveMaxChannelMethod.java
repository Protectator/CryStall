package ch.protectator.crystall;
import java.awt.image.BufferedImage;

import net.sourceforge.jiu.gui.awt.BufferedRGB24Image;

public class AdditiveMaxChannelMethod implements BlendMethod {
	
	private int[][][] max;
	private int channels;
	private int width;
	private int height;
	private int type;

	public AdditiveMaxChannelMethod(int nbChannels, int width, int height, int type) {
		this.channels = nbChannels;
		this.width = width;
		this.height = height;
		this.type = type;
		this.max = new int[width][][];
		int x, y, channel;
		for (x = 0; x < width; x++) {
			this.max[x] = new int[height][];
			for (y = 0; y < height; y++) {
				this.max[x][y] = new int[channels];
				for (channel = 0; channel < channels; channel++) {
					this.max[x][y][channel] = 0;	
				}
			}
		}
	}

	@Override
	public void iterate(BufferedRGB24Image newFrame) {
		int x, y, channel;
		int[][] column;
		int[] pixel;
		for (x = 0; x < width; x++) {
			column = max[x];
			for (y = 0; y < height; y++) {
				pixel = column[y];
				for (channel = 0; channel < channels; channel++) {
					max[x][y][channel] = maxIterate(pixel[channel], newFrame.getSample(channel, x, y));
				}
			}
		}
	}

	@Override
	public void finish() {
		return;
	}

	@Override
	public String getName() {
		return "maxChannels";
	}
	
	@Override
	public BufferedRGB24Image getPixels() {
		BufferedRGB24Image result = new BufferedRGB24Image(new BufferedImage(width, height, type));
		int x, y, channel;
		for (x = 0; x < width; x++) {
			for (y = 0; y < height; y++) {
				for (channel = 0; channel < channels; channel++) {
					result.putSample(channel, x, y, max[x][y][channel]);
				}
			}
		}
		return result;
	}
	
	private int maxIterate(int lastWeight, int newValue) {
		return Math.max(lastWeight, newValue);
	}

}
