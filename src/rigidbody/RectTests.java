package rigidbody;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class RectTests {
	
	Rectangle rect;
	Rect r,r2;
	Matrix mat;
	JButton butt;
	JFrame jf;
	Image img;
	Graphics g2;
	int DELAY = 10;
	
	public static void main(String[] args) {
		new RectTests();
	}
	
	public RectTests(){
		jf = new JFrame("test");
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
		img = jf.createImage(1000, 1000);
		g2 = img.getGraphics();
		butt = new JButton("repaint");
		
//		System.out.println(new Vec(0,0,12).cross(new Vec(13,11)));
		
		@SuppressWarnings("serial")
		JPanel jp = new JPanel(){
			public void paintComponent(Graphics g){
//				System.out.println(checkCollision(r,r2));
//				checkCollision(r,r2);
				
				draw(g2);
				g.drawImage(img, 0, 0, null);
				
			}
		};
		jp.setPreferredSize(new Dimension(500,500));
		jf.setContentPane(jp);
		jp.add(butt);
		butt.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				DELAY = 200;
			}
			
		});
		
		r = new Rect(100,10,new Vec(120,220),new Vec(1,1),new Vec(0,0,0.0),Math.PI/2);
		r2 = new Rect(10,10,new Vec(500,400),new Vec(-1,0),new Vec(0,0,0.0),Math.PI/4);
		
		r.setMass(100);
		r2.setMass(100);
		
		jf.pack();
		
		Thread thread = new Thread(new Runnable(){
			public void run(){
				try{
					while(true){
						r.update();r2.update();
						jf.repaint();
						System.out.println(angMomentum().mag());
						Thread.sleep(DELAY);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		
		thread.start();
		
	}
	
	public void draw(Graphics g){
		g.setColor(Color.white);
		g.fillRect(0, 0, 1000, 1000);
		g.setColor(Color.black);
		Vec[] a = checkCollision(r2,r);
		Vec[] b = checkCollision(r,r2);
		if(a[0]!=null){
			collide(r2,r,a[0],a[1],1).draw(g, a[0]);
			g.setColor(Color.red);
			r.drawRect(g);
			r2.drawRect(g);
			a[1].scaleV(100).draw(g, a[0], Color.orange);
			momentum().draw(g, new Vec(100,100), Color.orange);
			
		}else if(b[0]!=null){
			collide(r,r2,b[0],b[1],1).draw(g, b[0]);
			g.setColor(Color.red);
			r.drawRect(g);
			r2.drawRect(g);
			b[1].scaleV(100).draw(g, b[0], Color.orange);
			momentum().draw(g, new Vec(100,100), Color.orange);

			
		}else{
			
			r.drawRect(g);
			r2.drawRect(g);
			momentum().draw(g, new Vec(100,100), Color.orange);
		}
		
//		Vec[] bum = r.getCorners();
//		Vec[] hoose = r.getNormals();
//		for(Vec v:bum)v.draw(g2, Vec.ZERO, Color.yellow);
//		for(Vec v:hoose)v.draw(g2, r.pos, Color.yellow);
	}
	
//	public Vec checkCollision(Rect a, Rect b){
//		
//		Vec apos = a.pos;
//		Vec[] corners = b.getCorners();
//		Vec v = new Vec();
//		mat = new Matrix(-a.angPos);
//		for(int i=0;i<4;i++){
//			v = mat.multiply2D(corners[i].minus(apos));
////			System.out.println(Math.abs(v.x));
//			if(Math.abs(v.x)<=a.w/2 && Math.abs(v.y)<=a.h/2){
//				mat = new Matrix(a.angPos);
//				return mat.multiply2D(v).plus(a.pos);
//			}
//		}
//		
//		return null;
//	}
	
	public Vec[] checkCollision(Rect a, Rect b){
		
		Vec[] vecs = new Vec[2];
		
		Vec apos = a.pos;
		Vec[] corners = b.getCorners();
		Vec v = new Vec();
		mat = new Matrix(-a.angPos);
		//find collision point:
		for(int i=0;i<4;i++){
			v = mat.multiply2D(corners[i].minus(apos));
//			System.out.println(Math.abs(v.x));
			if(Math.abs(v.x)<=a.w/2 && Math.abs(v.y)<=a.h/2){
//				mat = new Matrix(a.angPos);
//				vecs[0] = mat.multiply2D(v).plus(a.pos);
				vecs[0] = v;
				break;
			}
		}
		
		//find collision normal:
		if(vecs[0]==null)return vecs;
		double[] dists = new double[4];
		
		dists[0] = a.h/2 - vecs[0].y;
		dists[1] = a.w/2 - vecs[0].x;
		dists[2] = vecs[0].y + a.h/2;
		dists[3] = vecs[0].x + a.w/2;
		
		int mini=0;
		for(int i=0;i<4;i++){
			if(dists[i]<dists[mini])mini=i;
		}
		
//		switch(mini){
//		case 0: vecs[1] = new Vec(0,1);
//			vecs[0].add(new Vec(0,dists[0]));
//			break;
//		case 1: vecs[1] = new Vec(1,0);
//			vecs[0].add(new Vec(dists[1],0));
//			break;
//		case 2: vecs[1] = new Vec(0,-1);
//			vecs[0].add(new Vec(0,-dists[2]));
//			break;
//		case 3: vecs[1] = new Vec(-1,0);
//			vecs[0].add(new Vec(-dists[3],0));
//			break;
//		}
//		System.out.println(dists[3]);
		if(mini==0)vecs[1] = new Vec(0,1);
		else if(mini==1)vecs[1] = new Vec(1,0);
		else if(mini==2)vecs[1] = new Vec(0,-1);
		else if(mini==3)vecs[1] = new Vec(-1,0);
		
		mat = new Matrix(a.angPos);
		vecs[0] = mat.multiply2D(vecs[0]).plus(apos);
		vecs[1] = mat.multiply2D(vecs[1]);
		return vecs;
		
	}
	
	public Vec collide(Rect a, Rect b, Vec c, Vec nn, double res){
		
		// to find the normal unit vector:
		Vec n = nn.scaleV(1);
//		n=a.getNormals()[2];
		
		
		//collision
		Vec ra = c.minus(a.pos), rb = c.minus(b.pos),
			ua =  a.vel.plus(a.angVel.cross(ra)), ub = b.vel.plus(b.angVel.cross(rb));

		Vec Un = n.scaleV(ub.minus(ua).dot(n));
		if(Un.dot(n)>0)return Vec.ZERO;
	
		double denom = a.invMass+b.invMass+(rb.cross(n).cross(rb).scaleV(b.invMOI)
											.plus(ra.cross(n).cross(ra).scaleV(a.invMOI))
											.dot(n));
		Vec j = Un.scaleV((1+res)/denom);
//		System.out.println(denom);
		a.impulse(j);
		b.impulse(j.scaleV(-1));
		
		a.angImpulse(j,ra);
		b.angImpulse(j.scaleV(-1),rb);
		
//		System.out.println(ub.minus(ua).mag());
//		System.out.println(j+"\n"+a.angVel+"\n"+b.angVel);
		
		return j;
		
	}
	
	public Vec momentum(){
		return r.vel.scaleV(1/r.invMass).plus(r2.vel.scaleV(1/r2.invMass));
	}
	
	public Vec angMomentum(){
		Vec lr = r.pos.cross(r.vel.scaleV(1/r.invMass)).plus(r.angVel.scaleV(1/r.invMOI));
		Vec lr2 = r2.pos.cross(r2.vel.scaleV(1/r2.invMass)).plus(r2.angVel.scaleV(1/r2.invMOI));
		return lr.plus(lr2);
	}
	
}
