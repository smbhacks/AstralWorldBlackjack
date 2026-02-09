package dk.astralworldminigame.pk;

import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;

public class Canvas extends GameCanvas implements Runnable {
	public Canvas(Main midlet) {
		super(false);
		this.midlet = midlet;
		setFullScreenMode(true);
	}

	private void cleanup() {
		buttonImage = null;
		coinCounterImage = null;
		statusImage = null;
		bgImage = null;
		System.gc();
	}
	
	public void run() {
		while (!stopCanvas) {
			updateScreen(getGraphics());
			Key.resetKeyStates();
			try {
				Thread.sleep(sleepTime);				
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			gameTick();
		}
		cleanup();
	}

	public void restartGame() {
		midlet.playMidi("/underworld.mid", -1);
		level = 1;
		world = 1;
		initialCoinCounter = coinCounter = 50;
		state = INIT_STATE;
		endOfGameTextCounter = 0;
		stateTimer = 30;
		statusMessage = "Starting money: " + coinCounter;		
	}
	
	public void start() {
		int i = 0;
		for(int rank = 2; rank <= 14; rank++) {
			for(int suit = 0; suit <= 3; suit++) {
				masterDeck[i++] = new Card(rank, suit);
			}
		}
		try {
			buttonImage = Image.createImage("/buttons.png");
			coinCounterImage = Image.createImage("/coincounter.png");
			statusImage = Image.createImage("/status.png");
			bgImage = Image.createImage("/bg.png");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		endOfGameMenu.addToMenu("Try again", new Menu.Action() {
			public void execute() {
				restartGame();
			}
		});
		endOfGameMenu.addToMenu("Quit", new Menu.Action() {
			public void execute() {
				midlet.destroyApp(true);
				stopCanvas = true;
				midlet.notifyDestroyed();
			}
		});
		restartGame();
		Thread runner = new Thread(this);
		runner.start();
	}

	private void drawCards(Graphics g, Vector cards, int y) {
		for(int i = 0; i < cards.size(); i++) {
			int x = Main.screenWidth / 2 + i * 12 - (40 + (cards.size()-1) * 12) / 2;
			Card card = (Card)cards.elementAt(i);
			card.draw(g, x, y);
		}		
	}
	
	private int countDigits(int num) {
		int digits = 0;
		do {
			num /= 10;
			digits++;
		} while(num != 0);
		return digits;
	}
	
	private void drawCoinCounter(Graphics g) {
		// figure out coin shine
		int shineState = 0;
		if(coinCounterFrame < 30)
			shineState = 0;
		else if (coinCounterFrame < 40)
			shineState = 1;
		else if (coinCounterFrame < 50)
			shineState = 2;
		else if (coinCounterFrame < 60)
			shineState = 1;
		// drawing
		g.drawRegion(coinCounterImage, shineState*10, 0, 10, 14, 0, 4, 4, 0);
		int t = coinCounter;
		for(int digitPlace = countDigits(coinCounter); digitPlace > 0; digitPlace--) {
			int digit = t % 10;
			t /= 10;
			g.drawRegion(coinCounterImage, 30 + digit*16, 0, 16, 16, 0, 20 + (digitPlace-1)*16, 4, 0);
		}
	}
	
	private void drawStage(Graphics g) {
		if(world > 4 || level > 4)
			return;
		int x, y = 0;
		x = (world-1)*48;
		y = (level-1)*16;
		g.drawRegion(statusImage, x, y, 48, 16, 0, Main.screenWidth - 48 - 4, 4, 0);
	}
	
	private void drawBg(Graphics g) {
		int whereY = 0; 
		do {
			int whereX = 0;
			int x = 0;
			if(world < 3)
				x = gameTickCounter % 16;
			else
				x = (gameTickCounter % 8)*2;
			do {
				g.drawRegion(bgImage, x, 0, 176, 224, 0, whereX, whereY, 0);
			} while((whereX += 176) < Main.screenWidth);
		} while((whereY += 224) < Main.screenHeight);
	}
	
	private void updateScreen(Graphics g) {
		drawBg(g);
		drawCards(g, playerHand, Main.screenHeight - 70 - 16);
		drawCards(g, dealerHand, 16);
		if(drawPlayerButtons) {
			g.drawRegion(buttonImage, 0, 0, 31, 16, 0, 0, Main.screenHeight-16, 0); //hit
			g.drawRegion(buttonImage, 31, 0, 39, 16, 0, Main.screenWidth-39, Main.screenHeight-16, 0); //stand		
			drawPlayerButtons = false;
		}
		drawCoinCounter(g);
		drawStage(g);
		g.setColor(0xFFFFFF);
		g.drawString(statusMessage, Main.screenWidth / 2, Main.screenHeight - 16, Graphics.HCENTER | Graphics.TOP);
		endOfGameMenu.handleDrawing(g, Main.screenWidth / 2, Main.screenHeight / 2, gameTickCounter);
		flushGraphics();
	}

	private Card getRandomCardFromDeck() {
		int random_index = rand.nextInt(deck.size());
		Card card = (Card)deck.elementAt(random_index);
		deck.removeElementAt(random_index);
		return card;
	}

	private int countCards(Vector cards) {
		// takes ace into consideration
		int sum = 0;
		for(int i = 0; i < cards.size(); i++) {
			Card card = (Card)cards.elementAt(i);
			if(card.rank == 14) {
				//ace
				if(sum + 11 > 21)
					sum += 1;
				else
					sum += 11;
			}
			else {
				//not ace
				if(card.rank >= 10)
					sum += 10;
				else
					sum += card.rank;
			}
		}
		System.out.println(sum);
		return sum;
	}
	
	private void gameTick() {
		gameTickCounter++;
		coinCounterFrame += 2;
		if(coinCounterFrame >= 60)
			coinCounterFrame = 0;
		if(coinToAdd < 0) {
			coinToAdd++;
			coinCounter--;
		}
		else if(coinToAdd > 0) {
			coinToAdd--;
			coinCounter++;
		}
		if(stateTimer != 0) {
			stateTimer--;
			return;
		}
		switch(state) {
		case INIT_STATE:
			endOfGameTextCounter = 0;
			dealerCardRevealState = 0;
			playerHand.removeAllElements();
			dealerHand.removeAllElements();
			deck.removeAllElements();
			for(int i = 0; i < 52; i++) {
				masterDeck[i].backShown = false;
				deck.addElement(masterDeck[i]);
			}
			playerHand.addElement(getRandomCardFromDeck());
			dealerHand.addElement(getRandomCardFromDeck());
			Card dealerFirstCard = (Card)dealerHand.firstElement();
			dealerFirstCard.backShown = true;
			dealerHand.addElement(getRandomCardFromDeck());
			stateTimer = 30;
			state = PLAYER_TURN_STATE;
			if(coinCounter > 5)
				bet = coinCounter / 2;
			else
				bet = coinCounter;
			statusMessage = "Bet: " + bet;
			if(bet < coinCounter) {
				coinToAdd -= bet;
			}
			else {
				coinToAdd -= coinCounter;
			}
			break;
		case PLAYER_TURN_STATE:
			drawPlayerButtons = true;
			if(Key.states[Key.SOFT_LEFT_KEY].pressed) {
				playerHand.addElement(getRandomCardFromDeck());
				stateTimer = 15;
				drawPlayerButtons = false;
				int playerValue = countCards(playerHand);
				if(playerValue == 21) {
					state = DEALER_TURN_STATE;
					statusMessage = "Your hand: 21!";
				}
				else if(playerValue > 21) {
					state = PLAYER_BUST_STATE;
					stateTimer = 0;
					midlet.display.vibrate(250);
				}
				else {
					statusMessage = "Your hand: " + playerValue;
				}
			}
			else if(Key.states[Key.SOFT_RIGHT_KEY].pressed) {
				state = DEALER_TURN_STATE;
				stateTimer = 30;
				drawPlayerButtons = false;
				statusMessage = "Standing with " + countCards(playerHand);
			}
			break;
		case DEALER_TURN_STATE:
			switch(dealerCardRevealState) {
			case 0:
				Card firstCard = (Card)dealerHand.firstElement();
				dealerHand.removeElementAt(0);
				dealerHand.addElement(firstCard);
				dealerCardRevealState++;
				stateTimer = 30;
				break;
			case 1:
				Card revealCard = (Card)dealerHand.lastElement();
				revealCard.backShown = false;
				dealerCardRevealState++;
				stateTimer = 30;
				statusMessage = "Dealer: " + countCards(dealerHand);
				break;
			case 2:
				int dealerValue = countCards(dealerHand);
				if(dealerValue < 17) {
					dealerHand.addElement(getRandomCardFromDeck());
					dealerValue = countCards(dealerHand);
					if(dealerValue > 21) {
						statusMessage = "Dealer busted!";
						stateTimer = 20;
						state = YOU_WIN_STATE;
					}
					else {
						stateTimer = 30;
						statusMessage = "Dealer: " + dealerValue;
					}
				}
				else {
					int playerValue = countCards(playerHand);
					if(playerValue > dealerValue)
						state = YOU_WIN_STATE;
					else if(playerValue < dealerValue)
						state = YOU_LOSE_STATE;
					else {
						if(playerValue == 21 && playerHand.size() == 2 && dealerHand.size() != 2) {
							//player has Blackjack, but the dealer doesn't
							state = YOU_WIN_STATE;
						} else {
							//tie
							coinToAdd = bet;
							statusMessage = "Tie!";
							stateTimer = 40;
							state = INC_LEVEL_STATE;
						}
					}
				}
				break;
			}
			break;
		case PLAYER_BUST_STATE:
			statusMessage = "You busted!";
			stateTimer = 20;
			state = YOU_LOSE_STATE;
			break;
		case YOU_LOSE_STATE:
			statusMessage = "You lose..";
			stateTimer = 40;
			state = INC_LEVEL_STATE;
			break;
		case YOU_WIN_STATE:
			statusMessage = "You win!!";
			stateTimer = 40;
			state = INC_LEVEL_STATE;
			if(playerHand.size() == 2) {
				//blackjack win
				coinToAdd += bet + bet * 3/2;
			} else {
				//regular win
				coinToAdd += bet + bet;
			}
			break;
		case INC_LEVEL_STATE:
			state = INIT_STATE;
			if(coinCounter == 0)
				state = END_OF_GAME_STATE;
			else if(level++ == 4) {
				level = 1;
				if(world++ == 4) {
					world = level = 4;
					state = END_OF_GAME_STATE;
				}
			}
			if(level == 1 && world == 3)
				midlet.playMidi("/cultureshock.mid", -1);
			break;
		case END_OF_GAME_STATE:
			stateTimer = 30;
			switch(endOfGameTextCounter) {
			case 0:
				statusMessage = "Game over!";
				midlet.stopMidi();
				endOfGameTextCounter++;
				break;
			case 1:
				if(coinCounter == 0) {
					statusMessage = "You've lost it all...";
					midlet.playMidi("/death.mid", 1);
				}
				else if(coinCounter <= initialCoinCounter) {
					statusMessage = "You didn't make any profit.";
				} else {
					statusMessage = "Your profit: " + (coinCounter - initialCoinCounter) + "!";
					midlet.playMidi("/win.mid", 1);
				}
				stateTimer = 60;
				endOfGameTextCounter++;
				break;
			case 2:
				stateTimer = 0;
				playerHand.removeAllElements();
				dealerHand.removeAllElements();				
				endOfGameMenu.drawFlag = true;
				endOfGameMenu.checkInput();
				break;
			}
			break;
		}
	}
	
	protected void keyPressed(int keyCode) {
		Key.keyPressed(keyCode);
	}
	protected void keyReleased(int keyCode) {
		Key.keyReleased(keyCode);
	}
	
	private Menu endOfGameMenu = new Menu();
	private int endOfGameTextCounter;
	private int bet;
	private int coinToAdd;
	private int coinCounter;
	private int initialCoinCounter;
	private int level;
	private int world;
	//0: move card to the top
	//1: reveal card
	//2: count card, do other stuff, etc.
	private int dealerCardRevealState;
	private Image bgImage;
	private Image buttonImage;
	private Image coinCounterImage;
	private Image statusImage;
	//0: nothing, show level-world
	//1: bust
	//2: win
	//3: lose
	//4: tie
	private String statusMessage = new String();
	private int coinCounterFrame = 0;
	private int gameTickCounter = 0;
	private boolean drawPlayerButtons = false;
	private final int INIT_STATE = 0;
	private final int PLAYER_TURN_STATE = 1;
	private final int DEALER_TURN_STATE = 2;
	private final int PLAYER_BUST_STATE = 3;
	private final int YOU_LOSE_STATE = 4;
	private final int YOU_WIN_STATE = 5;
	private final int INC_LEVEL_STATE = 6;
	private final int END_OF_GAME_STATE = 7;
	private int state = 0;
	private int stateTimer = 0;
	private Card[] masterDeck = new Card[52];
	private Vector deck = new Vector(52);
	private Vector playerHand = new Vector(12);
	private Vector dealerHand = new Vector(12);
	private Random rand = new Random();
	private final int sleepTime = 30;
	private Main midlet;
	public boolean stopCanvas = false;
}