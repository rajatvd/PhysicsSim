package mousehandler;

import java.awt.event.MouseEvent;

import physicssim.Vec;


public class MousePan implements MouseState {
	
	private Vec start, end, oldPan, pan;
	
	public MousePan(){
		oldPan = new Vec();
		pan = new Vec();
	}
	
	/**
	 * Sets the start of the pan addition to the press point.
	 * o is not used
	 */
	public void mousePressAction(MouseEvent e) {
		oldPan.x = pan.x;
		oldPan.y = pan.y;
		start = new Vec(e.getX(),e.getY());
		end = new Vec(start.x,start.y);
	}
	
	/**
	 * Changes the pan Vec while dragging to (position of mouse)-(start) added to the oldPan Vec.
	 * o is assumed to be a Vec, and its coordinates are set as the pan's
	 * coordinates.
	 */
	public void mouseDragAction(MouseEvent e) {
		end.x=e.getX();
		end.y=e.getY();
		Vec p = oldPan.plus(end.minus(start));
		pan.x = p.x;
		pan.y = p.y;
	}
	
	/**
	 * Replaces the oldPan with the current pan Vec.
	 * o is not used
	 */
	public void mouseReleaseAction(MouseEvent e) {
		
	}
	
	/**
	 * Set the Vec object which will be treated as the pan Vec
	 * @param p - the Vec object to treat as pan
	 */
	public void setPanVec(Vec p){
		pan = p;
	}
}
