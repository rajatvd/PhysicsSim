package mousehandler;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;


public class MouseStateHandler {
	
	@SuppressWarnings("unused")
	private static final String VERSION = "1.0";
	
	private MouseState leftButtonState, rightButtonState;
	
	public MouseState getLeftState() {
		return leftButtonState;
	}
	
	/**
	 * @param s - the MouseState to set as leftButtonState
	 */
	public void setLeftState(MouseState s) {
		leftButtonState = s;
	}
	
	public MouseState getRightState() {
		return rightButtonState;
	}
	
	/**
	 * @param s - the MouseState to set as leftButtonState
	 */
	public void setRightState(MouseState s) {
		rightButtonState = s;
	}
	
	/**
	 * Perform the press action of either the left or right
	 * state, based on which mouse button was used
	 * @param e - MouseEvent of press
	 */
	public void pressAction(MouseEvent e){
		if(SwingUtilities.isRightMouseButton(e)){
			rightButtonState.mousePressAction(e);
		}else if(SwingUtilities.isLeftMouseButton(e)){
			leftButtonState.mousePressAction(e);
		}
	}
	
	/**
	 * Perform the drag action of either the left or right
	 * state, based on which mouse button was used
	 * @param e - MouseEvent of drag
	 */
	public void dragAction(MouseEvent e){
		if(SwingUtilities.isRightMouseButton(e)){
			rightButtonState.mouseDragAction(e);
		}else if(SwingUtilities.isLeftMouseButton(e)){
			leftButtonState.mouseDragAction(e);
		}
	}
	
	/**
	 * Perform the release action of either the left or right
	 * state, based on which mouse button was used
	 * @param e - MouseEvent of release
	 */
	public void releaseAction(MouseEvent e){
		if(SwingUtilities.isRightMouseButton(e)){
			rightButtonState.mouseReleaseAction(e);
		}else if(SwingUtilities.isLeftMouseButton(e)){
			leftButtonState.mouseReleaseAction(e);
		}
	}
	
	/**
	 * Perform the click action of either the left or right
	 * state, based on which mouse button was used
	 * @param e - MouseEvent of click
	 */
	public void clickAction(MouseEvent e) {
		if(SwingUtilities.isRightMouseButton(e)){
			rightButtonState.mouseClickAction(e);
		}else if(SwingUtilities.isLeftMouseButton(e)){
			leftButtonState.mouseClickAction(e);
		}	
	}
	
	/**
	 * Perform the move action of both the left and right
	 * state
	 * @param e - MouseEvent of move
	 */
	public void moveAction(MouseEvent e) {
		rightButtonState.mouseMoveAction(e);
		leftButtonState.mouseMoveAction(e);	
	}
	
	/**
	 * Calls the drawState function of both the left and right 
	 * mouse button states of this handler.
	 * @param g - Graphics object to pass into drawState
	 */
	public void drawStates(Graphics g){
		rightButtonState.drawState(g);
		leftButtonState.drawState(g);
	}

	
}
