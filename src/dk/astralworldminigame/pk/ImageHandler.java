package dk.astralworldminigame.pk;

import java.io.IOException;

import javax.microedition.lcdui.Image;

public class ImageHandler {
	private Image image = null;

	public Image getImage() {
		return image;
	}
	public void createImage(String path) {
		if(image == null) {
			try {
				image = Image.createImage(path);
			} catch (IOException ioex) {
				System.out.println(ioex.getMessage());
			}
		}
	}
	public void unloadImage() {
		image = null;
	}
}
