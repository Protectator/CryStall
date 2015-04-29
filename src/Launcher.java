import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.zip.Deflater;

import javax.imageio.ImageIO;

import net.sourceforge.jiu.codecs.CodecMode;
import net.sourceforge.jiu.codecs.ImageLoader;
import net.sourceforge.jiu.codecs.InvalidFileStructureException;
import net.sourceforge.jiu.codecs.InvalidImageIndexException;
import net.sourceforge.jiu.codecs.PNGCodec;
import net.sourceforge.jiu.codecs.UnsupportedCodecModeException;
import net.sourceforge.jiu.codecs.UnsupportedTypeException;
import net.sourceforge.jiu.data.PixelImage;
import net.sourceforge.jiu.gui.awt.BufferedRGB24Image;
import net.sourceforge.jiu.ops.OperationFailedException;

import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;

public class Launcher {
	static String path = "frames/";
	static String name = "frame_";
	static String ext = "bmp";

	public static void main(String[] args) {
		long time = System.currentTimeMillis();
		int frameNb;
		File videoFile = new File("test.mp4");
		BufferedImage frame;
		BufferedRGB24Image image;
		try {
			frame = FrameGrab.getFrame(videoFile, 0);
			
			int w = frame.getWidth();
			int h = frame.getHeight();
			int[][][] pixels = new int[w][][];
			
			for (int x = 0; x < w; x++) {
				pixels[x] = new int[h][];
				for (int y = 0; y < h; y++) {
					pixels[x][y] = new int[3];
					pixels[x][y][0] = 0;
					pixels[x][y][1] = 0;
					pixels[x][y][2] = 0;
				}
			}

			// For each frame
			for (frameNb = 0; frameNb < 2; frameNb++) { 
				System.out.println("Computing frame " + frameNb);
				try {
					image = new BufferedRGB24Image(FrameGrab.getFrame(videoFile, frameNb));
					// Adding sample
					for (int x = 0; x < w; x++) {
						for (int y = 0; y < h; y++) {
							pixels[x][y][0] += image.getSample(0, x, y);
							pixels[x][y][1] += image.getSample(1, x, y);
							pixels[x][y][2] += image.getSample(2, x, y);
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
			
			// Saving average
			for (int x = 0; x < w; x++) {
				for (int y = 0; y < h; y++) {
					out.putSample(0, x, y, pixels[x][y][0] / frameNb); // Red
					out.putSample(1, x, y, pixels[x][y][1] / frameNb); // Green
					out.putSample(2, x, y, pixels[x][y][2] / frameNb); // Blue
				}
			}
			
			PNGCodec codec = new PNGCodec();
			try {
				codec.setFile("out.png", CodecMode.SAVE);
				codec.setImage(out);
				codec.setCompressionLevel(Deflater.BEST_COMPRESSION);
				try {
					codec.process();
					System.out.println("Finished");
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
