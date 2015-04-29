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

		for (frameNb = 0; frameNb < 8; frameNb++) { 
			System.out.println("Computing frame " + frameNb);
			try {
				frame = FrameGrab.getFrame(videoFile, frameNb);
				ImageIO.write(frame, ext, new File(path + name + frameNb + "." + ext));
			} catch (IOException | JCodecException e) {
				System.out.println("Something went wrong :/");
				e.printStackTrace();
			} catch (ArrayIndexOutOfBoundsException e) {
				break;
			}
		}
		System.out.println("Finished extracting " + frameNb + " frames in " + (System.currentTimeMillis() - time)+" milliseconds");

		BufferedImage frame2;
		try {
			frame2 = FrameGrab.getFrame(videoFile, 0);
			int w = frame2.getWidth();
			int h = frame2.getHeight();

			HashMap<String, Integer> pixels = new HashMap<String, Integer>(frameNb*w*h);

			frame = FrameGrab.getFrame(videoFile, frameNb);
			
			System.out.println("Loading all frames");

			// Storing all frames
			for (int i = 0; i < frameNb; i++) {
				BufferedRGB24Image image = new BufferedRGB24Image(ImageIO.read(new File(path + name + i + "." + ext)));
				System.out.println("Loading frame " + i);
				for (int x = 0; x < w; x++) {
					for (int y = 0; y < h; y++) {
						int channels = 256*256*image.getSample(0, x, y) + 256*image.getSample(1, x, y) + image.getSample(2, x, y);
						pixels.put(i + "," + x + "," + y, channels);
					}
				}
			}
			
			System.out.println("Computing final frames");
			
			BufferedRGB24Image out = new BufferedRGB24Image(new BufferedImage(frame.getWidth(), frame.getHeight(), frame.getType()));
			
			// Computing final frame
			int c1 = 0;
			int c2 = 0;
			int c3 = 0;
			int channels;
			for (int x = 0; x < w; x++) {
				System.out.println("x : " + x);
				for (int y = 0; y < h; y++) {
					c1 = 0;
					c2 = 0;
					c3 = 0;
					for (int i = 0; i < frameNb; i++) {
						channels = pixels.get(i + "," + x + "," + y);
						c1 += (channels/256/256)%256;
						c2 += (channels/256)%256;
						c3 += channels%256;
					}
					c1 /= frameNb;
					c2 /= frameNb;
					c3 /= frameNb;
					out.putSample(0, x, y, c1); // Red
					out.putSample(1, x, y, c2); // Green
					out.putSample(2, x, y, c3); // Blue
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

		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (JCodecException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

	}

}
