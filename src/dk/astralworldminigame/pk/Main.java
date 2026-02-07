package dk.astralworldminigame.pk;

import java.io.InputStream;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.midlet.MIDlet;

public class Main extends MIDlet {
	public Main() {
		Key.initAllKeys();
		display = Display.getDisplay(this);
		titleCanvas = new TitleCanvas(this);
	}

	public void startApp() {
		display.setCurrent(titleCanvas);
	}
	
	public void pauseApp() {

	}

	public void destroyApp(boolean unconditional) {
		
	}

	public void startGame() {
		if (gameCanvas == null) {
			gameCanvas = new Canvas(this);
		}
		gameCanvas.start();
		display.setCurrent(gameCanvas);
	}
	
	public void createBackground(Graphics g) {
		g.setColor(0x000000);
		g.fillRect(0, 0, Main.jarWidth, Main.jarHeight);
		g.setColor(0xFFFFFF);
		g.drawRect(0, 0, Main.jarWidth, Main.jarHeight);
	}	
	
	public void stopMidi() {
		if(midiPlayer != null) {
			try {
				midiPlayer.stop();
			} catch (MediaException e) {
				System.out.println(e.getMessage());
			}
			midiPlayer.close();
			midiPlayer = null;
		}		
	}
	public void playMidi(String path) {
		if(!musicEnabled)
			return;
		try {
			stopMidi();
			InputStream is = getClass().getResourceAsStream(path);
			midiPlayer = Manager.createPlayer(is, "audio/midi");
			midiPlayer.setLoopCount(-1);
			midiPlayer.start();
			is.close();
			is = null;
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	private Player midiPlayer;
	private TitleCanvas titleCanvas;
	private Canvas gameCanvas;
	private Display display;
	public boolean musicEnabled = true;
	public static final int jarHeight = 220;
	public static final int jarWidth = 176;
}