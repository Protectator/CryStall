import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.zip.Deflater;

import net.sourceforge.jiu.codecs.CodecMode;
import net.sourceforge.jiu.codecs.PNGCodec;
import net.sourceforge.jiu.codecs.UnsupportedCodecModeException;
import net.sourceforge.jiu.gui.awt.BufferedRGB24Image;
import net.sourceforge.jiu.ops.OperationFailedException;

import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;

public class Launcher {
	static final int RED_CHANNEL = 0;
	static final int GREEN_CHANNEL = 1;
	static final int BLUE_CHANNEL = 2;

	public static void main(String[] args) {
		long startingTime = System.currentTimeMillis();
		File videoFile = new File("test.mp4");
		BufferedImage frame;
		try {
			frame = FrameGrab.getFrame(videoFile, 0);
			
			int w = frame.getWidth();
			int h = frame.getHeight();
			int[][][] pixels = new int[w][][];
			
			for (int x = 0; x < w; x++) {
				pixels[x] = new int[h][];
				for (int y = 0; y < h; y++) {
					pixels[x][y] = new int[3];
					pixels[x][y][RED_CHANNEL] = 0;
					pixels[x][y][GREEN_CHANNEL] = 0;
					pixels[x][y][BLUE_CHANNEL] = 0;
				}
			}

			int frameNb;
			// For each frame
			for (frameNb = 0; true; frameNb++) { 
				System.out.println("Sampling frame " + frameNb);
				BufferedRGB24Image image;
				try {
					image = new BufferedRGB24Image(FrameGrab.getFrame(videoFile, frameNb));
					// Adding sample
					for (int x = 0; x < w; x++) {
						for (int y = 0; y < h; y++) {
							pixels[x][y][RED_CHANNEL] += image.getSample(RED_CHANNEL, x, y);
							pixels[x][y][GREEN_CHANNEL] += image.getSample(GREEN_CHANNEL, x, y);
							pixels[x][y][BLUE_CHANNEL] += image.getSample(BLUE_CHANNEL, x, y);
						}
					}
				} catch (IOException | JCodecException e) {
					System.out.println("Something went wrong :/");
					e.printStackTrace();
				} catch (ArrayIndexOutOfBoundsException e) {
					break;
				}
			}
			
			BufferedRGB24Image out = new BufferedRGB24Image(new BufferedImage(frame.getWidth(), frame.getHeight(), frame.getType()));
			
			System.out.println("Computing average");
			// Saving average
			for (int x = 0; x < w; x++) {
				for (int y = 0; y < h; y++) {
					out.putSample(RED_CHANNEL, x, y, pixels[x][y][RED_CHANNEL] / frameNb); // Red
					out.putSample(GREEN_CHANNEL, x, y, pixels[x][y][GREEN_CHANNEL] / frameNb); // Green
					out.putSample(BLUE_CHANNEL, x, y, pixels[x][y][BLUE_CHANNEL] / frameNb); // Blue
				}
			}
			
			PNGCodec codec = new PNGCodec();
			try {
				System.out.println("Saving result");
				codec.setFile("out.png", CodecMode.SAVE);
				codec.setImage(out);
				codec.setCompressionLevel(Deflater.BEST_COMPRESSION);
				try {
					codec.process();
					long endingTime = System.currentTimeMillis();
					System.out.println("Finished");
					System.out.println("Total time : " + (endingTime - startingTime)/1000 + " seconds");
				} catch (OperationFailedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (UnsupportedCodecModeException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException | JCodecException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

}
