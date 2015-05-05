package ch.protectator.crystall;
import net.sourceforge.jiu.gui.awt.BufferedRGB24Image;

public interface BlendMethod {
	public void iterate(BufferedRGB24Image newFrame);
	public void finish();
	public BufferedRGB24Image getPixels();
	public String getName();
}
