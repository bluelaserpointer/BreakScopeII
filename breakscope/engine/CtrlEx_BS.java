package engine;

import static java.awt.event.KeyEvent.*;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.BitSet;

import core.GHQ;
import stage.ControlExpansion;

public class CtrlEx_BS extends ControlExpansion{
	//mouse
	@Override
	public void mousePressed(MouseEvent e) {
		changeCommndBoolByKeyCode(e.getButton(),true);
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		changeCommndBoolByKeyCode(e.getButton(),false);
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		
	}
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		
	}
	
	//key
	final long SPELL_TAP_DUR = 200L;
	
	private static final int COMMAND_MAXID = 30;
	final int commandCode[] = {VK_A,VK_W,VK_D,VK_S,MouseEvent.BUTTON1,VK_SHIFT};
	final int commandID[] = {LEFT,UP,RIGHT,DOWN,SHOT,LEAP};
	final String commandName[] = {"MOVE_LEFT","MOVE_UP","MOVE_RIGHT","MOVE_DOWN","SHOT","LEAP"};
	static final int
		LEFT = 0,UP = 1,RIGHT = 2,DOWN = 3,
		SHOT = 10,LEAP = 11,
		SPELL_CHARA1 = 20,SPELL_CHARA2 = 21,SPELL_CHARA3 = 22,SPELL_CHARA4 = 23;
	private final BitSet commandBools = new BitSet(COMMAND_MAXID);
	private long key_1_time,key_2_time,key_3_time,key_4_time;
	boolean key_editMode;
	
	@Override
	public void keyPressed(KeyEvent e) {
		final int KEY_CODE = e.getKeyCode();
		switch(KEY_CODE){
		case VK_1:
		case VK_2:
		case VK_3:
		case VK_4:
			switch(KEY_CODE){
			case VK_1:
				if(!GHQ.isExpired_time(key_1_time,SPELL_TAP_DUR))
					commandBools.set(SPELL_CHARA1);
				key_1_time = GHQ.getNowTime();
				break;
			case VK_2:
				if(!GHQ.isExpired_time(key_2_time,SPELL_TAP_DUR))
					commandBools.set(SPELL_CHARA2);
				key_2_time = GHQ.getNowTime();
				break;
			case VK_3:
				if(!GHQ.isExpired_time(key_3_time,SPELL_TAP_DUR))
					commandBools.set(SPELL_CHARA3);
				key_3_time = GHQ.getNowTime();
				break;
			case VK_4:
				if(!GHQ.isExpired_time(key_4_time,SPELL_TAP_DUR))
					commandBools.set(SPELL_CHARA4);
				key_4_time = GHQ.getNowTime();
				break;
			}
			break;
		case VK_F6:
			if(Engine_BS.editMode) {
				Engine_BS.editMode = false;
				GHQ.clearStopEvent();
			}else if(GHQ.isNoStopEvent()) {
				Engine_BS.editMode = true;
				GHQ.stopScreen_noAnm();
			}
			break;
		default:
			changeCommndBoolByKeyCode(KEY_CODE,true);
		}
	}
	@Override
	public void keyReleased(KeyEvent e) {
		final int KEY_CODE = e.getKeyCode();
		switch(KEY_CODE) {
		case VK_1:
		case VK_2:
		case VK_3:
		case VK_4:
		case VK_F5:
		case VK_F6:
			break;
		default:
			changeCommndBoolByKeyCode(KEY_CODE,false);
		}
	}
	int pullSpellUser() {
		if(commandBools.get(SPELL_CHARA1)) {
			commandBools.clear(SPELL_CHARA1);
			return 0;
		}
		if(commandBools.get(SPELL_CHARA2)) {
			commandBools.clear(SPELL_CHARA2);
			return 1;
		}
		if(commandBools.get(SPELL_CHARA3)) {
			commandBools.clear(SPELL_CHARA3);
			return 2;
		}
		if(commandBools.get(SPELL_CHARA4)) {
			commandBools.clear(SPELL_CHARA4);
			return 3;
		}
		return NONE;
	}
	
	//control
	@Override
	public void reset() {
		commandBools.clear();
		key_1_time = key_2_time = key_3_time = key_4_time = NONE;
	}
	public boolean getCommandBool(int commandID) {
		return commandBools.get(commandID);
	}
	public boolean pullCommandBool(int commandID) {
		if(commandBools.get(commandID)) {
			commandBools.clear(commandID);
			return true;
		}else
			return false;
	}
	private final void changeCommndBoolByKeyCode(int keyCode,boolean onoff) {
		for(int i = 0;i < commandCode.length;i++) {
			if(commandCode[i] == keyCode) {
				commandBools.set(commandID[i],onoff);
				break;
			}
		}
	}
}
