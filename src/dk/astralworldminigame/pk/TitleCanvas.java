package dk.astralworldminigame.pk;

import java.io.IOException;
import java.util.Vector;

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
		case STORY_INIT_STATE:
			midlet.playMidi("/story.mid", -1);
			state = STORY_STATE;
			break;
		case STORY_STATE:
			if(Key.states[Key.SOFT_LEFT_KEY].down)
				state = TITLE_INIT;
			else if(storyTimer++ >= 150) {
				storyTimer = 0;
				storyCounter++;
				storyTexts.removeAllElements();
				switch(storyCounter) {
				case 0:
					storyTexts.addElement("Mario is on the spaceship home....");					
					break;
				case 1:
					storyTexts.addElement("...but turns out an enemy");
					storyTexts.addElement("intruded the ship by accident");
					storyTexts.addElement("without noticing!");
					break;
				case 2:
					storyTexts.addElement("There was nothing to do");
					storyTexts.addElement("though, and they got bored");
					storyTexts.addElement("pretty quickly.");
					break;
				case 3:
					storyTexts.addElement("Luckily, Mario remembered the");
					storyTexts.addElement("playing cards in his pocket.");
					break;
				case 4:
					storyTexts.addElement("Mamma-mia! These aren't ");
					storyTexts.addElement("the deck of cards I had!");
					storyTexts.addElement("Who switched them out?!");
					break;
				case 5:
					storyTexts.addElement("...at this point, whatever.");
					storyTexts.addElement("Let's just play something.");
					break;
				case 6:
					state = TITLE_INIT;
					break;
				}
			}
			break;
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
					titleXPos = Main.screenWidth / 2 - 176 / 2;
					state = MENU_STATE;					
				}
			}
			if(titleXPos < Main.screenWidth / 2 - 176 / 2) {
				titleXPos += 3;				
			}
			else {
				titleXPos = Main.screenWidth / 2 - 176 / 2;
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
		int x = titleTickCounter % 2 == 0 ? titleXPos : Main.screenWidth - titleXPos - 176;
		if(state == STORY_STATE) {
			if(storyTimer < 10)
				g.setColor(0x000000);
			else if(storyTimer < 20 || storyTimer > 140)
				g.setColor(0x9B77F3);
			else
				g.setColor(0xFFFFFF);
			for(int i = 0; i < storyTexts.size(); i++)
				g.drawString((String)storyTexts.elementAt(i), Main.screenWidth/2, Main.screenHeight/2 + i*16, Graphics.HCENTER | Graphics.TOP);
			
			g.setColor(0xFFFFFF);
			g.drawString("Skip story", 4, Main.screenHeight - 16, 0);
		}
		else {
			g.drawRegion(titleImage, 0, 0, 176, 75, 0, x, 0, 0);
		}
		mainMenu.handleDrawing(g, Main.screenWidth/2, Main.screenHeight/2, titleTickCounter);
		flushGraphics();
	}
   
	protected void keyPressed(int keyCode) {
		Key.keyPressed(keyCode);
	}
	protected void keyReleased(int keyCode) {
		Key.keyReleased(keyCode);
	}
	
	private Menu mainMenu = new Menu();
	private Vector storyTexts = new Vector();
	private int storyTimer = 999;
	private int storyCounter = -1;
	private final int STORY_INIT_STATE = 0;
	private final int STORY_STATE = 1;
	private final int TITLE_INIT = 2;
	private final int TITLE_APPROACHING_STATE = 3;
	private final int MENU_STATE = 4;
	private int state = 0;
	private int titleTickCounter = 0;
	private int titleXPos = -176;
	private Image titleImage;
	public boolean stopCanvas = false;
	private int sleepTime = 30;
}