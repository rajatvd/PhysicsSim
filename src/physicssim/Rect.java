package physicssim;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Rect extends RigidBody{
	
	Rectangle bounds;
	
	Vec angVel;
	double angPos;
	int w,h;
	double invMOI=1;// inverse moment of inertia
	
	public Rect(int w, int h, Vec poss, Vec vell, Vec angVell, double angPoss){
		pos=new Vec(poss);
		vel=new Vec(vell);
		angVel=new Vec(angVell);
		angPos=angPoss;
		bounds=new Rectangle(-w/2,-h/2,w,h);
		this.w = w;
		this.h = h;
		invMass=1;
	}
	
	public Rect(int w, int h, Vec poss, double angPoss){
		this(w,h,poss,new Vec(),new Vec(),angPoss);
	}
	
	public Rect(int w, int h){
		this(w,h,new Vec(),0);
	}
	
	public void drawBody(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		g2.translate(pos.x, pos.y);
		g2.rotate(angPos);
		
		g2.fill(bounds);
		
		g2.rotate(-angPos);
		g2.translate(-pos.x, -pos.y);
	}
	
	public void update(){
		pos.add(vel);
		angPos+=angVel.z;
	}
	
	/**
	 * 
	 * @return An array of Vecs with 4 elements. These Vecs point to the 4 corners of the
	 * Rect from the origin. 
	 */
	public Vec[] getCorners(){
		Vec[] corners = new Vec[4];
		Matrix mat = new Matrix(angPos);
		corners[0] = mat.multiply2D(new Vec(w/2,h/2)).add(pos);
		corners[1] = mat.multiply2D(new Vec(-w/2,h/2)).add(pos);
		corners[2] = mat.multiply2D(new Vec(-w/2,-h/2)).add(pos);
		corners[3] = mat.multiply2D(new Vec(w/2,-h/2)).add(pos);
		return corners;	
	}
	
	/**
	 * 
	 * @return An array of Vecs with 4 elements. These Vecs are perpendicular to
	 * the 4 sides of the Rect. They are unit vectors.
	 */
	public Vec[] getNormals(){
		Vec[] normals = new Vec[4];
		Matrix mat = new Matrix(angPos);
		normals[0] = mat.multiply2D(new Vec(0,1));
		normals[1] = mat.multiply2D(new Vec(1,0));
		normals[2] = mat.multiply2D(new Vec(0,-1));
		normals[3] = mat.multiply2D(new Vec(-1,0));
		return normals;	
	}
	
	/**
	 * This only impulses to the momentum, not angular momentum
	 * @param i - impulse vector
	 */
	public void impulse(Vec i){
		vel.add(i.scaleV(invMass));
	}
	/**
	 * This only impulses angular momentum
	 * @param i - impulse vector
	 * @param r - relative position vector of the impulse point 
	 * from centre of mass
	 */
	public void angImpulse(Vec i, Vec r){
		angVel.add(r.cross(i).scaleV(invMOI));
	}
	
	/**
	 * Sets the mass of the Rect and also updates the moment of inertia
	 * @param m - new mass
	 */
	public void setMass(double m){
		invMass = 1/m;
		invMOI = 12/(m*(h*h + w*w));
		System.out.println(1/invMOI);
	}
	
}
