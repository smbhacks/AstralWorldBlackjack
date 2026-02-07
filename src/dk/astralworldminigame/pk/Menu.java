package dk.astralworldminigame.pk;

import java.util.Vector;

import javax.microedition.lcdui.Graphics;

public class Menu {
	public static interface Action {
		void execute();
	}	
	
	public static class Item {
		public Item(String string, Action action) {
			this.string = string;
			this.action = action;
		}
		public Item(String string) {
			this.string = string;
		}
		
		public void execute() {
			action.execute();
		}
		
		public void setAction(Action action) {
			this.action = action;
		}
		
		public String string;
		private Action action;
	}
	
	public void addToMenu(String string, Action action) {
		Item item = new Item(string, action);
		items.addElement(item);
	}
	public void addToMenu(Item item) {
		items.addElement(item);
	}
	
	public void handleDrawing(Graphics g, int x, int y, int frameCounter) {
		if(drawFlag == false)
			return;
		
		g.setColor(0xFFFFFF);
		for(int i = 0; i < items.size(); i++) {
			if(selection == i && frameCounter/4 % 2 == 0)
				g.setColor(0x9B77F3);
			Item item = (Item)items.elementAt(i);
			g.drawString(item.string, x, y + i*16, Graphics.HCENTER | Graphics.TOP);
			if(selection == i)
				g.setColor(0xFFFFFF);
		}
		
		drawFlag = false;
	}
	
	public void checkInput() {
		if(selection < items.size()-1 && Key.states[Key.JOY_DOWN].pressed) 
			selection++;
		if(selection > 0 && Key.states[Key.JOY_UP].pressed) 
			selection--;
		
		if(Key.states[Key.JOY_ENTER].pressed) {
			Item selectedItem = (Item)items.elementAt(selection);
			selectedItem.action.execute();
		}
	}
	
	public int selection = 0;
	public boolean drawFlag = false;
	public Vector items = new Vector();
}
