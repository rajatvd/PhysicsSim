package mousehandler;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

public interface MouseState {
	/*
	 * Interface for a mouse state.
	 * Example: In paint, the mouse can act as a pencil, brush or fill tool,
	 * 			each of those are MouseStates
	 * MouseHandler handles a set of mouse states, each of which
	 * performs a particular task when a mouse event occurs.
	 */
	
	/**
	 * Perform the press action of this MouseState
	 * @param e - MouseEvent of press
	 */
	public void mousePressAction(MouseEvent e);
	
	/**
	 * Perform the drag action of this MouseState
	 * @param e - MouseEvent of drag
	 */
	public void mouseDragAction(MouseEvent e);
	
	/**
	 * Perform the release action of this MouseState
	 * @param e - MouseEvent of release
	 */
	public void mouseReleaseAction(MouseEvent e);
	
	/**
	 * Draw something based on the MouseState
	 * @param g - Graphics to draw the state with.
	 */
	public void drawState(Graphics g);
	
}
