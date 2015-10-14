package physicssim;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import mousehandler.MouseState;

public class MouseVecCreator implements MouseState {
	
	private Vec start, end, vec;
	private boolean hasVec, isCreatingVec = false;
	public Color vecColor = Color.yellow;
	
	
	/**
	 * Sets the starting point of a Vec to create.
	 */
	public void mousePressAction(MouseEvent e) {
		hasVec = false;
		start = new Vec(e.getX(),e.getY());
		end = new Vec(e.getX(),e.getY());
		vec = end.minus(start);
	}
	
	/**
	 * Dynamically updates a Vec under creation.
	 */
	public void mouseDragAction(MouseEvent e) {
		end.x=e.getX();
		end.y=e.getY();
		vec = end.minus(start);
		isCreatingVec = true;
	}
	
	/**
	 * Creates a vec and stores it as an available Vec.
	 */
	public void mouseReleaseAction(MouseEvent e) {
		hasVec = true;
		isCreatingVec = false;
	}
	
	/**
	 * @return Whether a Vec is available or not.
	 */
	public boolean hasVec(){
		return hasVec;
	}
	
	/**
	 * @return The created Vec, if available, null otherwise.
	 */
	public Vec getVec(){
		if(hasVec){
			hasVec = false;
			return new Vec(vec);
		}else{
			return null;
		}
	}
	
	/**
	 * @return The origin of the created Vec
	 */
	public Vec getOrigin(){
		return new Vec(start);
	}
	
	/**
	 * Draws the Vec if it is being created, from the mouse press position
	 */
	public void drawState(Graphics g) {
		if(isCreatingVec)vec.draw(g, start, vecColor);
	}

	public void mouseClickAction(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseMoveAction(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
