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

	static BlendMethod[] methods;

	public static void main(String[] args) {
		
		methods = new BlendMethod[]{new AverageMethod(), new AdditiveMinChannelMethod(), new AdditiveMaxChannelMethod()};
		int methodsL = methods.length;
		
		long startingTime = System.currentTimeMillis();
		File videoFile = new File("test.mp4");
		BufferedImage frame;
		try {
			frame = FrameGrab.getFrame(videoFile, 0);

			int w = frame.getWidth();
			int h = frame.getHeight();
			int[][][][] pixels = new int[w][][][];

			for (int x = 0; x < w; x++) {
				pixels[x] = new int[h][][];
				for (int y = 0; y < h; y++) {
					pixels[x][y] = new int[methodsL][];
					for (int methodNb = 0; methodNb < methodsL; methodNb++) {
						pixels[x][y][methodNb] = new int[3];
						pixels[x][y][methodNb] = methods[methodNb].initialize();
					}
				}
			}

			int frameNb;
			int[] pixel;
			// For each frame
			BufferedRGB24Image image;
			for (frameNb = 0; true; frameNb++) { 
				System.out.println("Sampling frame " + frameNb);
				try {
					image = new BufferedRGB24Image(FrameGrab.getFrame(videoFile, frameNb));
					// Adding sample
					for (int x = 0; x < w; x++) {
						for (int y = 0; y < h; y++) {
							pixel = new int[]{image.getSample(0, x, y), image.getSample(1, x, y), image.getSample(2, x, y)};
							for (int methodNb = 0; methodNb < methodsL; methodNb++) {
								pixels[x][y][methodNb] = methods[methodNb].iterate(frameNb, pixels[x][y][methodNb], pixel);
							}
						}
					}
				} catch (IOException | JCodecException e) {
					System.out.println("Something went wrong :/");
					e.printStackTrace();
				} catch (ArrayIndexOutOfBoundsException e) {
					break;
				}
			}

			BufferedRGB24Image[] out = new BufferedRGB24Image[methodsL];
			for (int methodNb = 0; methodNb < methodsL; methodNb++) {
				out[methodNb] = new BufferedRGB24Image(new BufferedImage(frame.getWidth(), frame.getHeight(), frame.getType()));
			}
			for (int x = 0; x < w; x++) {
				for (int y = 0; y < h; y++) {
					for (int methodNb = 0; methodNb < methodsL; methodNb++) {
						pixel = methods[methodNb].finish(frameNb, pixels[x][y][methodNb]);
						out[methodNb].putSample(0, x, y, pixel[0]);
						out[methodNb].putSample(1, x, y, pixel[1]);
						out[methodNb].putSample(2, x, y, pixel[2]);
					}
				}
			}
			
			// TODO from now

			System.out.println("Saving result");
			PNGCodec codec = new PNGCodec();
			try {
				for (int methodNb = 0; methodNb < methodsL; methodNb++) {
					codec.setFile("out_" + methods[methodNb].getName() + ".png", CodecMode.SAVE);
					codec.setImage(out[methodNb]);
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
