package corepresence.java;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class FrameIconList {

	public static BufferedImage getImage(int size, BufferedImage img) {
		Image tmp = img.getScaledInstance(size, size, Image.SCALE_SMOOTH);
		BufferedImage dimg = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = dimg.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return dimg;
	}
}
