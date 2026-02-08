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
		midlet.playMidi("/underworld.mid");
		level = 1;
		world = 1;
		coinCounter = 50;
		Thread runner = new Thread(this);
		runner.start();
	}

	private void drawCards(Graphics g, Vector cards, int y) {
		for(int i = 0; i < cards.size(); i++) {
			int x = Main.jarWidth / 2 + i * 12 - (40 + (cards.size()-1) * 12) / 2;
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
		int x, y = 0;
		x = (world-1)*48;
		y = (level-1)*16;
		g.drawRegion(statusImage, x, y, 48, 16, 0, Main.jarWidth - 48 - 4, 4, 0);
	}
	
	private void drawBg(Graphics g) {
		int x = gameTickCounter % 16;
		g.drawRegion(bgImage, x, 0, 176, 220, 0, 0, 0, 0);
	}
	
	private void updateScreen(Graphics g) {
		drawBg(g);
		drawCards(g, playerHand, Main.jarHeight - 70 - 16);
		drawCards(g, dealerHand, 16);
		if(drawPlayerButtons) {
			g.drawRegion(buttonImage, 0, 0, 31, 16, 0, 0, Main.jarHeight-16, 0); //hit
			g.drawRegion(buttonImage, 31, 0, 39, 16, 0, Main.jarWidth-39, Main.jarHeight-16, 0); //stand		
			drawPlayerButtons = false;
		}
		drawCoinCounter(g);
		drawStage(g);
		g.setColor(0xFFFFFF);
		g.drawString(statusMessage, Main.jarWidth / 2, Main.jarHeight - 16, Graphics.HCENTER | Graphics.TOP);
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
			statusMessage = "";
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
				}
				else if(playerValue > 21) {
					state = PLAYER_BUST_STATE;
					stateTimer = 0;
				}
			}
			else if(Key.states[Key.SOFT_RIGHT_KEY].pressed) {
				state = DEALER_TURN_STATE;
				stateTimer = 30;
				drawPlayerButtons = false;
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
				break;
			case 2:
				int dealerValue = countCards(dealerHand);
				if(dealerValue < 17) {
					dealerHand.addElement(getRandomCardFromDeck());
					if(countCards(dealerHand) > 21) {
						statusMessage = "Dealer busted!";
						stateTimer = 20;
						state = YOU_WIN_STATE;
					}
					else {
						stateTimer = 30;
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
			if(level++ == 4) {
				level = 1;
				if(world++ == 4) {
					state = END_OF_GAME_STATE;
				}
			}			
			break;
		case END_OF_GAME_STATE:
			System.out.println("Game end!");
			break;
		}
	}
	
	protected void keyPressed(int keyCode) {
		Key.keyPressed(keyCode);
	}
	protected void keyReleased(int keyCode) {
		Key.keyReleased(keyCode);
	}
	
	private int bet = 15;
	private int coinToAdd;
	private int coinCounter;
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