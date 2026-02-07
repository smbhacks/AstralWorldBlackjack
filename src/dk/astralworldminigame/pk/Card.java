package dk.astralworldminigame.pk;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

public class Card {
	//2-10
	//11: jack (goomba)
	//12: queen (koopa)
	//13: king (bowser)
	//14: ace
	public int rank;
	//0: coin
	//1: mushroom
	//2: flower
	//3: star
	public int suit;
	public boolean backShown = false;
	public static ImageHandler cardImageHandler = new ImageHandler();
	
	public Card(int rank, int suit) {
		this.rank = rank;
		this.suit = suit;
	}
	public void draw(Graphics g, int x, int y) {
		cardImageHandler.createImage("/card.png");
		Image cardImage = cardImageHandler.getImage();
		if(backShown) {
			g.drawRegion(cardImage, 145, 0, 40, 70, 0, x, y, 0); //blank card (back)
		}
		else {
			g.drawRegion(cardImage, 0, 0, 40, 70, 0, x, y, 0); //blank card (front)
			g.drawRegion(cardImage, 41 + 8*(rank - 2), 0, 8, 8, 0, x+3, y+3, 0); //number on top
			g.drawRegion(cardImage, 41 + 8*(rank - 2), 0, 8, 8, Sprite.TRANS_ROT180, 40-8+x-3, 70-8+y-3, 0); //number on bottom
			switch(rank) {
			case 2:
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, 0, x+12, y+3, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, Sprite.TRANS_MIRROR_ROT180, 40-16+x-12, 70-16+y-3, 0);
				break;
			case 3:
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, 0, x+12, y+3, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, Sprite.TRANS_MIRROR_ROT180, 40-16+x-12, 70-16+y-3, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, 0, x+12, y+27, 0);
				break;
			case 4:
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, 0, x+3, y+11, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, 0, x+21, y+11, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, Sprite.TRANS_MIRROR_ROT180, 40-16+x-3, 70-16+y-11, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, Sprite.TRANS_MIRROR_ROT180, 40-16+x-21, 70-16+y-11, 0);
				break;
			case 5:
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, 0, x+3, y+11, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, 0, x+21, y+11, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, Sprite.TRANS_MIRROR_ROT180, 40-16+x-3, 70-16+y-11, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, Sprite.TRANS_MIRROR_ROT180, 40-16+x-21, 70-16+y-11, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, 0, x+12, y+27, 0);			
				break;
			case 6:
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, 0, x+3, y+11, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, 0, x+21, y+11, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, 0, x+3, y+27, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, 0, x+21, y+27, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, Sprite.TRANS_MIRROR_ROT180, 40-16+x-3, 70-16+y-10, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, Sprite.TRANS_MIRROR_ROT180, 40-16+x-21, 70-16+y-10, 0);
				break;
			case 7:
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, 0, x+12, y+20, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, 0, x+2, y+11, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, 0, x+22, y+11, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, 0, x+2, y+27, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, 0, x+22, y+27, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, Sprite.TRANS_MIRROR_ROT180, 40-16+x-2, 70-16+y-10, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, Sprite.TRANS_MIRROR_ROT180, 40-16+x-22, 70-16+y-10, 0);
				break;
			case 8:
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, 0, x+3, y+11, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, 0, x+21, y+11-8, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, 0, x+3, y+22, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, 0, x+21, y+22-8, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, Sprite.TRANS_MIRROR_ROT180, 40-16+x-3, 70-16+y-11, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, Sprite.TRANS_MIRROR_ROT180, 40-16+x-21, 70-16+y-11+8, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, Sprite.TRANS_MIRROR_ROT180, 40-16+x-3, 70-16+y-22, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, Sprite.TRANS_MIRROR_ROT180, 40-16+x-21, 70-16+y-22+8, 0);
				break;
			case 9:
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, 0, x+3, y+11, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, 0, x+21, y+11-8, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, 0, x+2, y+22, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, 0, x+21, y+22-8, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, Sprite.TRANS_MIRROR_ROT180, 40-16+x-3, 70-16+y-11, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, Sprite.TRANS_MIRROR_ROT180, 40-16+x-21, 70-16+y-11+8, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, Sprite.TRANS_MIRROR_ROT180, 40-16+x-2, 70-16+y-22, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, Sprite.TRANS_MIRROR_ROT180, 40-16+x-21, 70-16+y-22+8, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, 0, x+12, y+27, 0);
				break;			
			case 10:
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, 0, x+2, y+11, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, 0, x+21, y+11-8, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, 0, x+2, y+22, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, 0, x+21, y+22-8, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, Sprite.TRANS_MIRROR_ROT180, 40-16+x-2, 70-16+y-11, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, Sprite.TRANS_MIRROR_ROT180, 40-16+x-21, 70-16+y-11+8, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, Sprite.TRANS_MIRROR_ROT180, 40-16+x-2, 70-16+y-22, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, Sprite.TRANS_MIRROR_ROT180, 40-16+x-21, 70-16+y-22+8, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, 0, x+12, y+17, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, Sprite.TRANS_MIRROR_ROT180, 40-16+x-12, 70-16+y-17, 0);
				break;
			case 11:
				g.drawRegion(cardImage, 72, 24, 28, 30, 0, x+6, y+20, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, 0, x+22, y+2, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, Sprite.TRANS_MIRROR_ROT180, 40-16+x-22, 70-16+y-2, 0);
				break;
			case 12:
				g.drawRegion(cardImage, 40, 24, 32, 46, 0, x+4, y+13, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, 0, x+22, y+2, 0);
				break;
			case 13:
				g.drawRegion(cardImage, 104, 8, 34, 50, 0, x+2, y+18, 0);
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, 0, x+22, y+2, 0);
				break;			
			case 14:
				g.drawRegion(cardImage, 40 + 16*suit, 8, 16, 16, 0, x+12, y+27, 0);
				break;
			}
		}
	}
}
