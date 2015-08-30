package ch.protectator.crystall;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
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
		
		if (args.length < 1) {
			System.out.println("You must provide an input video file as argument");
			System.exit(1);
		}
		
		long startingTime = System.currentTimeMillis();
		File videoFile = new File(args[0]);
		BufferedImage frame;
		try {
			try {
				FrameGrab.getFrame(videoFile, 0);
			} catch (ArithmeticException | NullPointerException e) {
				System.out.println("Input format not supported");
				System.exit(1);
			} catch (FileNotFoundException e) {
				System.out.println("File not found");
				System.exit(1);
			}
			
			frame = FrameGrab.getFrame(videoFile, 0);

			int w = frame.getWidth();
			int h = frame.getHeight();
			int type = frame.getType();
			
			methods = new BlendMethod[]{new AverageMethod(3, w, h, type),
					new AdditiveMinChannelMethod(3, w, h, type),
					new AdditiveMaxChannelMethod(3, w, h, type),
					new MaxBrightnessMethod(3, w, h, type),
					new MostExtremeChannelMethod(3, w, h, type)};
			int methodsL = methods.length;
			int frameNb;
			// For each frame
			BufferedRGB24Image image;
			for (frameNb = 0; true; frameNb++) { 
				System.out.println("Sampling frame " + frameNb);
				try {
					image = new BufferedRGB24Image(FrameGrab.getFrame(videoFile, frameNb));
					System.out.print("Computing method ");
					for (int method = 0; method < methodsL; method++) {
						System.out.print(methods[method].getName() + "... ");
						methods[method].iterate(image);
					}
					System.out.println();
					
				} catch (IOException | JCodecException e) {
					System.out.println("Something went wrong :/");
					e.printStackTrace();
				} catch (ArrayIndexOutOfBoundsException e) {
					break;
				}
			}

			BufferedRGB24Image[] out = new BufferedRGB24Image[methodsL];
			for (int method = 0; method < methodsL; method++) {
				out[method] = methods[method].getPixels();
			}

			System.out.println("Saving result");
			PNGCodec codec = new PNGCodec();
			try {
				for (int method = 0; method < methodsL; method++) {
					codec.setFile("out_" + methods[method].getName() + ".png", CodecMode.SAVE);
					codec.setImage(out[method]);
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
