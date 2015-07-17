package physicssim;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;


public class Vec {
	
	public double x,y,z=0;
	public static final Vec ZERO = new Vec(0,0);
	
	public Vec(){
		x=0;y=0;z=0;
	}
	
	public Vec(String s){
		s = s.replace("(", "").replace(")", "");
		String[] nums = s.split(",");
		if(nums.length==2){
			x=Double.parseDouble(nums[0]);
			y=Double.parseDouble(nums[1]);
		}else if(nums.length==3){
			x=Double.parseDouble(nums[0]);
			y=Double.parseDouble(nums[1]);
			z=Double.parseDouble(nums[2]);
		}
	}
	
	public Vec(double xx, double yy, double zz){
		x=xx;y=yy;z=zz;
	}
	public Vec(double xx, double yy){
		x=xx;y=yy;z=0;
	}
	
	public Vec(Vec v) {
		x=v.x;
		y=v.y;
		z=v.z;
	}
	
	
	public Vec set(Vec v){
		x=v.x;
		y=v.y;
		z=v.z;
		return this;
	}
	
	public Vec add(Vec v){
		x+=v.x;
		y+=v.y;
		z+=v.z;
		return this;
	}
	
	public Vec add(double xx, double yy, double zz){
		x+=xx;
		y+=yy;
		z+=zz;
		return this;
	}
	
	public Vec add(double xx, double yy){
		x+=xx;
		y+=yy;
		return this;
	}
	
	public Vec plus(Vec v){
		return new Vec(x+v.x,y+v.y,z+v.z);
	}
	
	public Vec plus(double xx, double yy){
		return new Vec(x+xx,y+yy,z);
	}
	
	public Vec subtract(Vec v){
		x-=v.x;
		y-=v.y;
		z-=v.z;
		return this;
	}
	
	public Vec minus(Vec v){
		return new Vec(x-v.x,y-v.y,z-v.z);
	}
	
	public Vec minus(double xx, double yy){
		return new Vec(x-xx,y-yy,z);
	}
	
	public double dot(Vec v){
		return x*v.x + y*v.y + z*v.z;
	}
	
	public Vec scale(double f){
		x*=f;y*=f;z*=f;
		return this;
	}
	public Vec scaleV(double f){
		return new Vec(x*f,y*f,z*f);
	}
	
	public double mag(){
		return Math.sqrt(x*x+y*y+z*z);
	}
	
	public Vec cross(Vec v){
		return new Vec(y*v.z - z*v.y,
					   z*v.x - x*v.z,
					   x*v.y - y*v.x);
	}
	
	/**
	 * Draws a Vec using a Graphics object. The color of the graphics may change.
	 * @param g - Graphics object to draw the Vec
	 * @param origin - Starting point of the Vec
	 * @param c - Color of the Vec
	 */
	public void draw(Graphics g, Vec origin, Color c){
		int mag = (int) this.mag();
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(c);
		
		//transform
		g2.translate(origin.x, origin.y);
		g2.rotate(Math.atan2(y,x));
		
		//draw vector
		g2.drawLine(0, 0, mag, 0);
		int[] xs = {mag-10,mag-10,mag};
		int[] ys = {5,-5,0};
		g2.fillPolygon(xs,ys,3);
		
		//undo transform
		g2.rotate(-Math.atan2(y,x));
		g2.translate(-origin.x, -origin.y);
	}
	
	/**
	 * Draws a Vec using a Graphics object with a default color of orange. 
	 * The color of the graphics is changed to orange
	 * @param g - Graphics object to draw the Vec
	 * @param origin - Starting point of the Vec
	 */
	public void draw(Graphics g, Vec origin){
		int mag = (int) this.mag();
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.orange);
		double xx = x, yy = y;
		//transform
		g2.translate(origin.x, origin.y);
		g2.rotate(Math.atan2(yy,xx));
		
		//draw vector
		g2.drawLine(0, 0, mag, 0);
		int[] xs = {mag-10,mag-10,mag};
		int[] ys = {5,-5,0};
		g2.fillPolygon(xs,ys,3);
		
		//undo transform
		g2.rotate(-Math.atan2(yy,xx));
		g2.translate(-origin.x, -origin.y);
	}
	
	/**
	 * Format: (x,y,z)
	 */
	public String toString(){
		return "("+ x +","+ y +","+ z +")";
	}
	
}
