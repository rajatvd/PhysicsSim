package rigidbody;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Rect extends RigidBody{
	
	Rectangle bounds;
	
	Vec angVel;
	double angPos;
	int w,h;
	double invMOI=1;
	
	public Rect(int w, int h, Vec poss, Vec vell, Vec angVell, double angPoss){
		pos=poss;
		vel=vell;
		angVel=angVell;
		angPos=angPoss;
		bounds=new Rectangle(-w/2,-h/2,w,h);
		this.w = w;
		this.h = h;
		invMass=1;
	}
	
	public Rect(int w, int h, Vec poss, double angPoss){
		pos=poss;
		vel=new Vec();
		angVel=new Vec();
		angPos=angPoss;
		bounds=new Rectangle(-w/2,-h/2,w,h);
		this.w = w;
		this.h = h;
		invMass=1;
	}
	
	public Rect(int w, int h){
		pos=new Vec();
		vel=new Vec();
		angVel=new Vec();
		angPos=0;
		bounds=new Rectangle(-w/2,-h/2,w,h);
		this.w = w;
		this.h = h;
		invMass=1;
	}
	
	public void drawRect(Graphics g){
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
	
	public Vec[] getCorners(){
		Vec[] corners = new Vec[4];
		Matrix mat = new Matrix(angPos);
		corners[0] = mat.multiply2D(new Vec(w/2,h/2)).add(pos);
		corners[1] = mat.multiply2D(new Vec(-w/2,h/2)).add(pos);
		corners[2] = mat.multiply2D(new Vec(-w/2,-h/2)).add(pos);
		corners[3] = mat.multiply2D(new Vec(w/2,-h/2)).add(pos);
		return corners;	
	}
	
	public Vec[] getNormals(){
		Vec[] corners = new Vec[4];
		Matrix mat = new Matrix(angPos);
		corners[0] = mat.multiply2D(new Vec(0,1));
		corners[1] = mat.multiply2D(new Vec(1,0));
		corners[2] = mat.multiply2D(new Vec(0,-1));
		corners[3] = mat.multiply2D(new Vec(-1,0));
		return corners;	
	}
	
	public void impulse(Vec i){
		vel.add(i.scaleV(invMass));
	}
	public void angImpulse(Vec i, Vec r){
		angVel.add(r.cross(i).scaleV(invMOI));
	}
	
	public void setMass(double m){
		invMass = 1/m;
		invMOI = 12/(m*(h*h + w*w));
		System.out.println(1/invMOI);
	}
	
}
