package physicssim;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Vector;

public class Ball extends RigidBody{
	
	double r;
	
	public static Vector<RigidBody> allBalls = new Vector<RigidBody>();
	
	public Ball(RigidBody a){
		pos = new Vec(a.pos.x, a.pos.y);
		newPos = new Vec(a.newPos.x, a.newPos.y);
		vel = new Vec(a.vel.x, a.vel.y);
		c = a.c;
		invMass = a.invMass;
		r = 1;
		allBalls.add(this);
	}
	
	public Ball(){
		pos = new Vec(0,0);
		newPos = new Vec(0,0);
		vel = new Vec(0,0);
		acc = new Vec(0,0);
		newAcc = new Vec(0,0);
		r = 1;
		c = Color.white;
		invMass = 1;
		allBalls.add(this);
	}
	
	public Ball(double xx, double yy, double rr){
		this();
		pos = new Vec(xx,yy);
		r = rr;
	}
	
	public Ball(Vec p, double rr){
		this();
		pos = new Vec(p.x,p.y);
		r = rr;
	}
	
	public Ball(Vec p, double rr, double mass){
		this(p,rr);
		if(mass==0)invMass=1;
		else invMass = 1.0/mass;
	}
	
	public Ball(Vec p, Vec vell, double rr){
		this(p,rr);
		vel = new Vec(vell.x,vell.y);
	}
	
	public Ball(Vec p, Vec vell, double rr, double mass){
		this(p,vell,rr);
		if(mass==0)invMass=1;
		else invMass = 1.0/mass;
	}
	
	public Ball(Vec p, Vec vell, double rr, double masss, Color cc){
		this(p,vell,rr,masss);
		newPos.set(p);
		c=cc;	
	}
	
	public void drawBody(Graphics g){
		Color cc = g.getColor();
		g.setColor(c);
		g.fillOval((int)(pos.x-r),(int)(pos.y-r),
				(int)(2*r),(int)(2*r));
		g.setColor(cc);
	}
	
	public void update(){
		pos.add(vel);
	}
	
	public void impulse(Vec p){
		vel.add(p.scaleV(invMass));
	}
	
	public void verletUpdatePos(){
		pos.add(vel.plus(acc.scale(0.5)));		
	}
	
	public void verletUpdateVel(double grav){
		getNewAcc(grav);
		vel.add((acc.plus(newAcc)).scale(0.5));
		acc.set(newAcc);
	}
	
	
	public void getNewAcc(double grav){
		Vec p = new Vec();
		for(int i=0;i<allBalls.size();i++){
			Ball a = (Ball) allBalls.elementAt(i);
			if(a.equals(this))continue;
			
			Vec rad = this.pos.minus(a.pos);
			if(rad.mag()<a.r+this.r)return;
			p.add(rad.scaleV(-grav/(Math.pow(rad.mag(),3)*a.invMass)));
		}
		newAcc.set(p);
	}
	
	public static void updatePositions(){
		for(int i=0;i<allBalls.size();i++){
			Ball a = (Ball) allBalls.elementAt(i);
			a.pos.set(a.newPos);
		}
	}
	
}
