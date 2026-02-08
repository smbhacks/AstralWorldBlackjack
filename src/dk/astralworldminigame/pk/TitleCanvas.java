package dk.astralworldminigame.pk;

import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;

public class TitleCanvas extends GameCanvas implements Runnable {
    private Main midlet;

    public TitleCanvas(Main midlet) {
    	super(false);
        this.midlet = midlet;
        setFullScreenMode(true);        
        try {
			titleImage = Image.createImage("/title.png");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		Thread runner = new Thread(this);
		runner.start();        
    }
    
    private void cleanup() {
    	titleImage = null;
    	System.gc();
    }

	public void run() {	
		while(!stopCanvas) {
			updateScreen(getGraphics());
			Key.resetKeyStates();
			try {
				Thread.sleep(sleepTime);				
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			titleTick();
		}
		cleanup();
	}
    
	private void titleTick() {
		titleTickCounter++;
		switch(state) {
		case TITLE_INIT:
			midlet.playMidi("/profound.mid", -1);
			mainMenu.addToMenu("Start a new game", new Menu.Action() {
				public void execute() {
					midlet.startGame();
					stopCanvas = true;
				}
			});
			final Menu.Item musicToggleItem = new Menu.Item("Music: On");
			musicToggleItem.setAction(new Menu.Action() {
				public void execute() {
					if(midlet.musicEnabled) {
						midlet.stopMidi();
						midlet.musicEnabled = false;
						musicToggleItem.string = "Music: Off";
					}
					else {
						midlet.musicEnabled = true;
						midlet.playMidi("/profound.mid", -1);
						musicToggleItem.string = "Music: On";
					}
				}
			});
			mainMenu.addToMenu(musicToggleItem);
			mainMenu.addToMenu("Quit", new Menu.Action() {
				public void execute() {
					midlet.destroyApp(true);
					stopCanvas = true;
					midlet.notifyDestroyed();
				}
			});			
			state = TITLE_APPROACHING_STATE;
			break;
		case TITLE_APPROACHING_STATE:
			for(int i = 0; i < 23; i++) {
				if(Key.states[i].pressed) {
					titleXPos = Main.jarWidth / 2 - 176 / 2;
					state = MENU_STATE;					
				}
			}
			if(titleXPos < Main.jarWidth / 2 - 176 / 2) {
				titleXPos += 3;				
			}
			else {
				titleXPos = Main.jarWidth / 2 - 176 / 2;
				state = MENU_STATE;
			}
			break;
		case MENU_STATE:
			mainMenu.drawFlag = true;
			mainMenu.checkInput();
			break;
		}
	}
	
	private void updateScreen(Graphics g) {
		midlet.createBackground(g);
		int x = titleTickCounter % 2 == 0 ? titleXPos : Main.jarWidth - titleXPos - 176;
		g.drawRegion(titleImage, 0, 0, 176, 75, 0, x, 0, 0);
		mainMenu.handleDrawing(g, Main.jarWidth/2, Main.jarHeight/2, titleTickCounter);
		flushGraphics();
	}
   
	protected void keyPressed(int keyCode) {
		Key.keyPressed(keyCode);
	}
	protected void keyReleased(int keyCode) {
		Key.keyReleased(keyCode);
	}
	
	private Menu mainMenu = new Menu();
	private final int TITLE_INIT = 0;
	private final int TITLE_APPROACHING_STATE = 1;
	private final int MENU_STATE = 2;
	private int state = 0;
	private int titleTickCounter = 0;
	private int titleXPos = -176;
	private Image titleImage;
	public boolean stopCanvas = false;
	private int sleepTime = 30;
}