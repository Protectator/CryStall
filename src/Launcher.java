import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long time = System.currentTimeMillis();
		int i;
		File file = new File("test.mp4");
		BufferedImage frame;

		String path="frames/";
		String name="frame_";
		String ext="bmp";
		for (i = 0; i < 2; i++) { 
			System.out.println("Computing frame " + i);
			try {
				frame = FrameGrab.getFrame(file, i);
				ImageIO.write(frame, ext, new File(path + name + i + "." + ext));
			} catch (IOException | JCodecException e) {
				System.out.println("Something went wrong :/");
				e.printStackTrace();
			} catch (ArrayIndexOutOfBoundsException e) {
				break;
			}
		}
		System.out.println("Finished extracting " + i + " frames in " + (System.currentTimeMillis() - time)+" milliseconds");

		PixelImage image;
		BufferedImage frame2;
		try {
			frame2 = FrameGrab.getFrame(file, 0);
			int w = frame2.getWidth();
			int h = frame2.getHeight();

			int[][][][] pixels = new int[i-1][][][];
			try {
				frame = FrameGrab.getFrame(file, 0);
				BufferedRGB24Image out = new BufferedRGB24Image(new BufferedImage(frame.getWidth(), frame.getHeight(), frame.getType()));
				for (int nbFrame = 0; nbFrame < i-1; nbFrame++) {
					pixels[nbFrame] = new int[w][][];
					for (int x = 0; x < w; x++) {
						pixels[nbFrame][x] = new int[h][];
						for (int y = 0; y < h; y++) {
							pixels[nbFrame][x][y] = new int[3];
							for (int channel = 0; channel < 3; channel++) {
								image = ImageLoader.load(path + name + nbFrame + "." + ext);
								out.putSample(nbFrame, x, y, 123); // Blue
							}
							System.out.println("y: " + y);
						}
						System.out.println("x: " + x);
					}
					System.out.println("frame " + nbFrame);
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
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (JCodecException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InvalidFileStructureException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidImageIndexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedTypeException e) {
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
