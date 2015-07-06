package physicssim;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import mousehandler.MouseState;

public class MouseVecCreator implements MouseState {
	
	private Graphics2D g;
	private Vec start, end, vec;
	private boolean hasVec;
	public Color vecColor = Color.yellow;
	
	public void setGraphics(Graphics2D gg){
		g = gg;
	}
	
	/**
	 * Sets the starting point of a Vec to create.
	 */
	public void mousePressAction(MouseEvent e) {
		hasVec = false;
		start = new Vec(e.getX(),e.getY());
		end = new Vec(e.getX(),e.getY());
	}
	
	/**
	 * Dynamically updates a Vec under creation and draws it using
	 * the Graphics object.
	 */
	public void mouseDragAction(MouseEvent e) {
		end.x=e.getX();
		end.y=e.getY();
		vec = end.minus(start);
		vec.draw(g, start, vecColor);
	}
	
	/**
	 * Creates a vec and stores it as an available Vec.
	 */
	public void mouseReleaseAction(MouseEvent e) {
		hasVec = true;
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
	 * 
	 * @return The origin of the created Vec
	 */
	public Vec getOrigin(){
		return new Vec(start);
	}
	
}
