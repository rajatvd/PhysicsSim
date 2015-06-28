package rigidbody;

import java.awt.Color;
import java.awt.Graphics;

public class Ball extends RigidBody{
	
	double r;
	
	public Ball(double xx, double yy, double rr){
		pos = new Vec(xx,yy);
		vel = new Vec(0,0);
		r = rr;
		c = Color.white;
		invMass = 1;
	}
	
	public Ball(Vec p, double rr){
		pos = new Vec(p.x,p.y);
		vel = new Vec(0,0);
		r = rr;
		c = Color.white;
		invMass = 1;
	}
	
	public Ball(Vec p, double rr, double mass){
		pos = new Vec(p.x,p.y);
		vel = new Vec(0,0);
		r = rr;
		c = Color.white;
		if(mass==0)invMass=1;
		else invMass = 1.0/mass;
	}
	
	public Ball(Vec p, Vec vell, double rr){
		pos = new Vec(p.x,p.y);
		vel = new Vec(vell.x,vell.y);
		r = rr;
		c = Color.white;
		invMass = 1;
	}
	
	public Ball(Vec p, Vec vell, double rr, double masss){
		pos = new Vec(p.x,p.y);
		vel = new Vec(vell.x,vell.y);
		r = rr;
		c = Color.white;
		if(masss==0)invMass=1;
		else invMass = 1.0/masss;
	}
	
	public Ball(Vec p, Vec vell, double rr, double masss, Color cc){
		pos = new Vec(p.x,p.y);
		vel = new Vec(vell.x,vell.y);
		r = rr;
		c = cc;
		if(masss==0)invMass=1;
		else invMass = 1.0/masss;
	}
	
	public void drawBall(Graphics g){
		Color cc = g.getColor();
		g.setColor(c);
		g.fillOval((int)(pos.x-r),(int)(pos.y-r),
				(int)(2*r),(int)(2*r));
		g.setColor(cc);
	}
	
	public void update(){
		pos.x+=vel.x;pos.y+=vel.y;
	}
	
	public void impulse(Vec p){
		vel.add(p.scaleV(invMass));
	}
	
}
