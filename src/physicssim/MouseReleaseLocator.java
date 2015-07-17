package physicssim;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import mousehandler.MouseState;

public class MouseReleaseLocator implements MouseState {
	
	private Vec vec, pos = new Vec();
	private boolean hasVec;
	public boolean drawCrosshair;
	public Color color = Color.magenta;
	public int w=20,h=20;
	
	
	/**
	 * @return Whether a Vec is available or not.
	 */
	public boolean hasVec(){
		return hasVec;
	}
	
	/**
	 * @return The Vec of click position, if available, null otherwise.
	 */
	public Vec getVec(){
		if(hasVec){
			hasVec = false;
			return new Vec(vec);
		}else{
			return null;
		}
	}
	
	public void mouseClickAction(MouseEvent e) {
		
	}

	public void mousePressAction(MouseEvent e) {
		
	}

	public void mouseDragAction(MouseEvent e) {
		mouseMoveAction(e);
	}

	public void mouseReleaseAction(MouseEvent e) {
		vec = new Vec(e.getX(), e.getY());
		hasVec = true;		
	}

	public void mouseMoveAction(MouseEvent e) {
		pos.x = e.getX();
		pos.y = e.getY();
	}
	
	public void drawState(Graphics g) {
		g.setColor(color);
		g.drawOval((int)pos.x-w/2, (int)pos.y-h/2, w, h);
	}
	
}
