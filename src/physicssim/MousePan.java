package physicssim;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

import mousehandler.MouseState;



public class MousePan implements MouseState {
	
	private Vec start, end, oldPan, pan;
	
	public MousePan(){
		oldPan = new Vec();
		pan = new Vec();
	}
	
	/**
	 * Sets the start of the pan addition to the press point.
	 */
	public void mousePressAction(MouseEvent e) {
		oldPan.x = pan.x;
		oldPan.y = pan.y;
		start = new Vec(e.getX(),e.getY());
		end = new Vec(start.x,start.y);
	}
	
	/**
	 * Changes the pan Vec while dragging to (position of mouse)-(start) added to the oldPan Vec.
	 */
	public void mouseDragAction(MouseEvent e) {
		end.x=e.getX();
		end.y=e.getY();
		Vec p = oldPan.plus(end.minus(start));
		pan.x = p.x;
		pan.y = p.y;
	}
	
	/**
	 * Nothing is done on mouse release.
	 */
	public void mouseReleaseAction(MouseEvent e) {
		
	}
	
	/**
	 * Set the Vec object which will be treated as the pan Vec.
	 * @param p - the Vec object to treat as pan
	 */
	public void setPanVec(Vec p){
		pan = p;
	}

	public void drawState(Graphics g) {}

	public void mouseClickAction(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseMoveAction(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
