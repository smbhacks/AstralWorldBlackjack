package dk.astralworldminigame.pk;

public class Key {
	final static int NUM_KEY0 = 11;
	final static int NUM_KEY1 = 12;
	final static int NUM_KEY2 = 13;
	final static int NUM_KEY3 = 14;
	final static int NUM_KEY4 = 15;
	final static int NUM_KEY5 = 16;
	final static int NUM_KEY6 = 17;
	final static int NUM_KEY7 = 18;
	final static int NUM_KEY8 = 19;
	final static int NUM_KEY9 = 20;
	final static int HANG_UP_KEY = 0;
	final static int CALL_KEY = 1;
	final static int SOFT_LEFT_KEY = 5;
	final static int SOFT_RIGHT_KEY = 4;
	final static int JOY_ENTER = 6;
	final static int JOY_UP = 10;
	final static int JOY_DOWN = 9;
	final static int JOY_RIGHT = 7;
	final static int JOY_LEFT = 8;	
	
	public static class KeyState {
		boolean down = false;
		boolean pressed = false;
		boolean released = false;
	}
	public static KeyState[] states = new KeyState[23];
	public static void initAllKeys() {
		for(int i = 0; i < 23; i++) {
			states[i] = new KeyState();
		}
	}
	public static void resetKeyStates() {
		for(int i = 0; i < 23; i++) {
			Key.states[i].pressed = false;
			Key.states[i].released = false;
		}		
	}
	public static void keyPressed(int keyCode){
		if(keyCode > 0) {
			if(keyCode >= 48 && keyCode <= 57){
				states[keyCode - 37].pressed = true;
				states[keyCode - 37].down = true;
			}
			else{
				switch(keyCode){
				case 42:
					states[21].pressed = true;
					states[21].down = true;
					break;
				case 35:
					states[22].pressed = true;
					states[22].down = true;
					break;
				}			
			}
		}
		else{
			states[keyCode + 11].pressed = true;
			states[keyCode + 11].down = true;
		}
	}
	public static void keyReleased(int keyCode){
		if(keyCode > 0) {
			if(keyCode >= 48 && keyCode <= 57){
				states[keyCode - 37].pressed = false;
				states[keyCode - 37].down = false;
				states[keyCode - 37].released = true;
			}
			else{				
				switch(keyCode){
				case 42:
					states[21].pressed = false;
					states[21].down = false;
					states[21].released = true;
					break;
				case 35:
					states[22].pressed = false;
					states[22].down = false;
					states[22].released = true;
					break;
				}				
			}
		}
		else{
			states[keyCode + 11].pressed = false;
			states[keyCode + 11].down = false;
			states[keyCode + 11].released = true;
		}
	}
}
