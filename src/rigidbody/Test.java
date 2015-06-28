package rigidbody;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.*;

public class Test implements ChangeListener{
	
	//GUI fields
	Image img;
	Graphics g2;
	JPanel main, image, ballPreview;
	JFrame jf;
	
	//ball collection
	Vector<Ball> balls = new Vector<Ball>();
	
	Vec pos, end = new Vec();
	
	//coefficient of restitution, gravity contant, ball creation mass
	double res=1, grav=5, mass;
	
	//wall width and height, ball creation radius
	int wallx=1100, wally=650, radius=6;
	
	//thread delay
	final int DELAY = 10;
	
	//sliders
	//3rd argument is default value
	JSlider radiusS  = new JSlider(JSlider.HORIZONTAL, 0, 50, 6),
//			gravity = new JSlider(JSlider.HORIZONTAL, -200, 200, 50),
			resti   = new JSlider(JSlider.HORIZONTAL, 0, 100, 100),
			trail   = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
	
	//labels
	JLabel  massLab    = new JLabel("Mass:"),
			radiusLab  = new JLabel("Radius"),
			gravityLab = new JLabel("Gravity Strength:"),
			restiLab   = new JLabel("Coefficient of Restitution"),
			trailLab   = new JLabel("Trail"),
			energyLab  = new JLabel("Energy: ");
	
	JCheckBox wallsCB   = new JCheckBox("Walls"),
			  momentumCB = new JCheckBox("Show Momentum Vector");
	
	JSpinner gravitySpinner, massSpinner;
	
	JButton chooseColor = new JButton("Choose ball colour"),
			chooseBGColor = new JButton("Choose background colour"),
//			playPause = new JButton("Pause"),
			reset = new JButton("Reset");

	
	Color ballColor = Color.white,
		  vecColor = Color.yellow,
		  bgColor = new Color(0,0,0,255), 
		  rawBG = Color.black;//backgournd color without transparency
	
	boolean isDragging = false,//mouse dragging
			drawMomentum = false,
			walls = false;
	
	Thread thread;
	
	
	public static void main(String[] args) {
		new Test();
	}
	
	@SuppressWarnings("serial")
	public Test(){
		
		//jframe
		jf = new JFrame("test");
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
		
		//canvas image
		img = jf.createImage(wallx, wally);
		g2 = img.getGraphics();
		
		
		//Panels
		//simulation canvas
		image = new JPanel(){
			public void paintComponent(Graphics g){
				clearImage(g2);
				drawBalls(g2);
				g.drawImage(img, 0, 0, null);
				drawVecs(g);
			}
		};
		image.setPreferredSize(new Dimension(wallx,1500));
		
		//main container
		main = new JPanel();
		GroupLayout gl = new GroupLayout(main);
		main.setLayout(gl);
		main.setPreferredSize(new Dimension(wallx+230,wally+30));
		
		//ball preview container
		ballPreview = new JPanel(){
			public void paintComponent(Graphics g){
				//background
				g.setColor(rawBG);
				g.fillRect(0, 0, 100, 100);
				//preview ball
				g.setColor(ballColor);
				g.fillOval(50-radius, 50-radius, radius*2, radius*2);
			}
		};
		ballPreview.setPreferredSize(new Dimension(100,100));
		
//		gravity.setMajorTickSpacing(100);
//		gravity.setPaintTicks(true);
//		gravity.setPaintLabels(true);
//		gravity.addChangeListener(this);
		
		//checkbox
		wallsCB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				walls = wallsCB.isSelected();
			}
		});
		
		momentumCB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				drawMomentum = momentumCB.isSelected();
			}
		});
		
		
		//spinners
		//first argument of constructor is default value
		gravitySpinner = new JSpinner(new SpinnerNumberModel(5,-1000,1000,5.0));
		gravitySpinner.setPreferredSize(new Dimension(70,20));
		gravitySpinner.addChangeListener(this);
		
		massSpinner = new JSpinner(new SpinnerNumberModel(10,-1,1000,1.0));
		massSpinner.setPreferredSize(new Dimension(70,20));
		massSpinner.addChangeListener(this);
		
		//sliders
		resti.setMajorTickSpacing(20);
		resti.setPaintTicks(true);
		resti.setPaintLabels(true);
		resti.addChangeListener(this);
		
		trail.setMajorTickSpacing(50);
		trail.setPaintTicks(true);
		trail.setPaintLabels(true);
		trail.addChangeListener(this);
		
		radiusS.setMajorTickSpacing(10);
		radiusS.setPaintTicks(true);
		radiusS.setPaintLabels(true);
		radiusS.addChangeListener(this);
		
		//buttons
		chooseColor.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				ballColor = JColorChooser.showDialog(jf, "Choose ball colour", ballColor);
			}		
		});
		chooseBGColor.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				Color c = JColorChooser.showDialog(jf, "Choose ball colour", ballColor);
				bgColor = new Color(c.getRed(),c.getGreen(),c.getBlue(),255-trail.getValue());
				rawBG = c;
			}		
		});
		reset.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				balls.clear();
			}		
		});
//		playPause.addActionListener(new ActionListener(){
//			public void actionPerformed(ActionEvent arg0) {
//				if(playPause.getText().equals("Pause")){
//					thread.suspend();
//					playPause.setText("Play");
//				}else if(playPause.getText().equals("Play")){
//					thread.resume();
//					playPause.setText("Pause");
//				}
//			}		
//		});
		
		
		//Layout
		
		gl.setHorizontalGroup(
				gl.createSequentialGroup()
				.addComponent(image,
						GroupLayout.PREFERRED_SIZE, 
						GroupLayout.PREFERRED_SIZE,
				        GroupLayout.PREFERRED_SIZE)
				        //main jpanel with image 
				//parallel group of parameter tuners:
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.CENTER, false)
						.addGroup(gl.createSequentialGroup()
								.addComponent(reset)
//								.addComponent(playPause)
								)
						.addGroup(gl.createSequentialGroup()
								.addComponent(gravityLab)
								.addComponent(gravitySpinner,
										GroupLayout.PREFERRED_SIZE, 
										GroupLayout.PREFERRED_SIZE,
								        GroupLayout.PREFERRED_SIZE)
								)
						.addComponent(restiLab)
						.addComponent(resti)
						.addComponent(trailLab)
						.addComponent(trail)
						.addComponent(wallsCB)
						.addComponent(momentumCB)
						.addComponent(energyLab)
						.addComponent(ballPreview,
								GroupLayout.PREFERRED_SIZE, 
								GroupLayout.PREFERRED_SIZE,
						        GroupLayout.PREFERRED_SIZE)
						.addComponent(radiusLab)
						.addComponent(radiusS)
						.addGroup(gl.createSequentialGroup()
								.addComponent(massLab)
								.addComponent(massSpinner,
										GroupLayout.PREFERRED_SIZE, 
										GroupLayout.PREFERRED_SIZE,
								        GroupLayout.PREFERRED_SIZE)
								)
						.addComponent(chooseColor)
						.addComponent(chooseBGColor)
						)
				);
		
		gl.setVerticalGroup(
				gl.createParallelGroup()
				.addComponent(image,
						GroupLayout.PREFERRED_SIZE, 
						GroupLayout.PREFERRED_SIZE,
				        GroupLayout.PREFERRED_SIZE)
				.addGroup(gl.createSequentialGroup()
						.addGroup(gl.createParallelGroup()
								.addComponent(reset)
//								.addComponent(playPause)
								)
						.addGroup(gl.createParallelGroup()
								.addComponent(gravityLab)
								.addComponent(gravitySpinner,
										GroupLayout.PREFERRED_SIZE, 
										GroupLayout.PREFERRED_SIZE,
								        GroupLayout.PREFERRED_SIZE)
								)
						.addComponent(restiLab)
						.addComponent(resti)
						.addComponent(trailLab)
						.addComponent(trail)
						.addComponent(wallsCB)
						.addComponent(momentumCB)
						.addComponent(energyLab)
						.addComponent(ballPreview,
								GroupLayout.PREFERRED_SIZE, 
								GroupLayout.PREFERRED_SIZE,
						        GroupLayout.PREFERRED_SIZE)
						.addComponent(radiusLab)
						.addComponent(radiusS)
						.addGroup(gl.createParallelGroup()
								.addComponent(massLab)
								.addComponent(massSpinner,
										GroupLayout.PREFERRED_SIZE, 
										GroupLayout.PREFERRED_SIZE,
								        GroupLayout.PREFERRED_SIZE)
								)
						.addComponent(chooseColor)
						.addComponent(chooseBGColor)
						)
				);
		
		
		
		gl.setAutoCreateGaps(true);
		gl.setAutoCreateContainerGaps(true);
		
		//mouse response
		pos = new Vec();
		MouseAdapter mouse = new MouseAdapter(){
			
			public void mousePressed(MouseEvent e){
				pos.x = e.getX();
				pos.y = e.getY();
				mass = (Double) massSpinner.getValue();
			}
			public void mouseDragged(MouseEvent e){
				end.x = e.getX();
				end.y = e.getY();
				isDragging = true;
//				image.repaint();
			}
			public void mouseReleased(MouseEvent e){
//				int r = e.getButton();
//				Ball a = new Ball(pos, radius, 1/mass);
//				a.vel = new Vec(e.getX(), e.getY()).minus(pos).scale(0.1);
				isDragging = false;
				balls.add(new Ball(pos, 
						new Vec(e.getX(), e.getY(),0).minus(pos).scale(0.1),
						radius, 
						mass,
						ballColor));
			}
		};
		
		image.addMouseListener(mouse);
		image.addMouseMotionListener(mouse);
		
		//simulation thread
		thread = new Thread(new Runnable(){
			public void run(){
				while(true){
					try {
						update();
						jf.repaint();
						Thread.sleep(DELAY);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ArrayIndexOutOfBoundsException e) {
						e.printStackTrace();
						balls.clear();
					}
				}
			}
		});
		
		thread.start();
		
		jf.setContentPane(main);
		jf.pack();
		
	}
	
	public void drawVecs(Graphics g){
		if(isDragging)drawBallCreationVec(g);
		if(drawMomentum)drawMomentum(g);
	}
	
	//to draw velocity vector of particle about to be made
	public void drawBallCreationVec(Graphics g){
		(end.minus(pos)).draw(g, pos, vecColor);
	}
	
	//to draw total momentum vector from the center of mass
	public void drawMomentum(Graphics g){
		Ball b;
		Vec s = new Vec();
		for(int i=0;i<balls.size();i++){
			b=balls.get(i);
			s.add(b.vel.scaleV(1/b.invMass)); 
		}
		s.draw(g, centerOM(), vecColor);
	}
	
	//returns the center of mass of the collection of balls
	public Vec centerOM(){
		Vec s = new Vec();
		double m=0;
		Ball b;
		for(int i=0;i<balls.size();i++){
			b=balls.get(i);
			s.add(b.pos.scaleV(1/b.invMass));
			m+=1/b.invMass;
		}
		return s.scale(1/m);
	}
	
	//implementation for change listener
	public void stateChanged(ChangeEvent e) {
		Object source = e.getSource();
		if(source instanceof JSlider){
			JSlider jsl = (JSlider)e.getSource();
			if(jsl.equals(resti)){
				res = resti.getValue()/100.0;
			}else if(jsl.equals(trail)){
				bgColor = new Color(rawBG.getRed(),
									rawBG.getGreen(),
									rawBG.getBlue(),
									255-trail.getValue());
			}else if(jsl.equals(radiusS)){
				radius = radiusS.getValue();
			}
//			else if(jsl.equals(gravity)){
//				grav = gravity.getValue();
//			}
		}else if(source instanceof JSpinner){
			JSpinner js = (JSpinner)source;
			if(js.equals(gravitySpinner)){
				grav = (Double) gravitySpinner.getValue();
			}else if(js.equals(massSpinner)){
//				System.out.println("MASS CHANGED");
				mass = (Double) massSpinner.getValue();
			}
		}
	}
	
	
	public void clearImage(Graphics g){
		g.setColor(bgColor);
		g.fillRect(0, 0, 1700, 1500);
	}
	
	//main update function
	public void update() throws ArrayIndexOutOfBoundsException{
//		System.out.println(energy());
//		Vec ua, ub, p, vels, r;
		Ball a,b;
		for(int i=0;i<balls.size();i++){
			for(int j=0;j<balls.size();j++){
				if(i==j)continue;
				a = balls.elementAt(i);
				b = balls.elementAt(j);
				if(j>i)gravitate(a,b);
				if(checkCollision(a,b)){
					collide(a,b,res);
//					if(a.vel.x==0 && a.vel.y==0 && 
//					b.vel.x==0 && b.vel.y==0)continue;
//					while(checkCollision(a,b)){
////						a.pos.add(a.vel.scaleV(1/a.vel.mag()));
////						b.pos.add(b.vel.scaleV(1/b.vel.mag()));
//						a.update();b.update();
//					}
//					ua = a.vel;
//					ub = b.vel;
//					p = ua.scaleV(a.mass).plus(ub.scaleV(b.mass));
//					b.vel = p.minus((ub.minus(ua).scale(a.mass*res))).scale(1.0/(a.mass+b.mass));
//					a.vel = p.plus((ub.minus(ua).scale(b.mass*res))).scale(1.0/(a.mass+b.mass));
				}
//				gravitate(a,b);
//				r = b.pos.minus(a.pos);
//				if(r.mag()<Math.max(b.r, a.r)*2)continue;
//				a.vel.add(r.scaleV(grav*b.mass/Math.pow(r.mag(),3)));
//				b.vel.add(r.scaleV(-grav*a.mass/Math.pow(r.mag(),3)));
			}
			if(walls)checkWall(balls.elementAt(i));
			balls.elementAt(i).update();
		}
//		energyLab.setText("Kinetic Energy: " + (float)energy());
		energyLab.setText(String.format("Kinetic Energy: %.8g", energy()));
	}
	
	//draw all balls
	public void drawBalls(Graphics g){
		for(int i=0;i<balls.size();i++){
			balls.elementAt(i).drawBall(g);
		}
	}
	
	
	public boolean checkCollision(Ball a, Ball b){
		return a.pos.minus(b.pos).mag()<a.r+b.r;
	}
	
	//perform wall collision
	public void checkWall(Ball a){
		if(a.pos.x<=a.r){
			a.vel.x = -a.vel.x;
			a.pos.x=a.r;
		}
		if(a.pos.x>=wallx-a.r){
			a.vel.x = -a.vel.x;
			a.pos.x=wallx-a.r;
		}
		if(a.pos.y<=a.r){
			a.vel.y = -a.vel.y;
			a.pos.y=a.r;
		}
		if(a.pos.y>=wally-a.r){
			a.vel.y = -a.vel.y;
			a.pos.y=wally-a.r;
		}
	}
	
	//perform gravity update
	public void gravitate(Ball a, Ball b){
		Vec r = b.pos.minus(a.pos);
		//to prevent penetration gravity
		if(r.mag()<a.r+b.r)return;
		
		Vec i = r.scaleV(grav/(Math.pow(r.mag(),3)*b.invMass*a.invMass));
		
//		a.vel.add(r.scaleV(grav/(Math.pow(r.mag(),3)*b.invMass)));
//		b.vel.add(r.scaleV(-grav/(Math.pow(r.mag(),3)*a.invMass)));
		
		a.impulse(i);
		b.impulse(i.scale(-1));
	}
	
//		Vec ua = a.vel, ub = b.vel;
//		// p = maua + mbub
//		Vec p = ua.scaleV(a.mass).plus(ub.scaleV(b.mass));
//		Vec n = b.pos.minus(a.pos);
//		n.scale(1/(n.mag()));
//		// n = unit vector joining centres
////		System.out.println(n.mag());
//		// normal component of momentum
//		Vec pn = n.scaleV((float)p.dot(n));
//		Vec uan = n.scaleV((float)ua.dot(n)), ubn = n.scaleV((float)ub.dot(n));
		/*
		 *  OLD CALCULATIONS
		 *  va - vb = rest(ub - ua)
		 *  va = vb + rest(ub - ua)
		 *  p = mava + mbvb
		 *  p = mavb + marest(ub - ua) + mbvb
		 *  p - marest(ub - ua) = vb(ma + mb)
		 *  vb = [p - marest(ub - ua)]/(ma + mb)
		 *  va = [p - marest(ub - ua) + marest(ub - ua) + mbrest(ub - ua)]/(ma + mb)
		 *  va = [p + mbrest(ub - ua)]/(ma + mb)
		 *  
		 */
//		if(ub.minus(ua).dot(n)>0)return;
//		Vec van = pn.add(ubn.minus(uan).scale((float)b.mass*rest)).scale((float)1.0/(a.mass+b.mass));
//		Vec vbn = pn.minus(ubn.minus(uan).scale((float)a.mass*rest)).scale((float)1.0/(a.mass+b.mass));
//		a.vel = van.add(ua.subtract(uan));//.scale(1/(n.x*n.x+n.y*n.y));
//		b.vel = vbn.add(ub.subtract(ubn));//.scale(1/(n.x*n.x+n.y*n.y));
//		System.out.println(p.minus(a.vel.scaleV(a.mass).plus(b.vel.scaleV(b.mass))).mag());
		
		
	/*
	 * NEW CALCULATIONS:(BETTER)
	 * 
	 * ma, mb - masses
	 * ua, ub - initial velocities
	 * va, vb - final velocities
	 * 
	 * Relative velocities:
	 * U = ub - ua
	 * V = vb - va
	 * 
	 * Newton's Law of Restitution:
	 * V.n = -rest*U.n
	 * (n is unit vector normal to collision)
	 * 
	 * Conservation of Momentum:
	 * maua + mbub = mava + mbvb
	 * ma(ua - va) = mb(vb - ub)
	 * ia = -ib = i
	 * 
	 * where 'i' is the impulse
	 * 
	 *  (va = ua - i/ma)
	 * -(vb = ub + i/mb)
	 * 
	 * va - vb = ua - ub + i(1/ma + 1/mb)
	 * i = (-V + U)/(1/ma + 1/mb)
	 * i = U(1+rest)/(1/ma + 1/mb)
	 * 
	 */
	
	public void collide(Ball a, Ball b, double rest){
		//get vectors
		Vec ua = a.vel, ub = b.vel;
		Vec U = ub.minus(ua);
		Vec n = b.pos.minus(a.pos);
		if(n.mag()==0)return;
		n.scale(1/n.mag());
		
		//check if collision is proper
		if(U.dot(n)>0)return;
		
		//find impulse
		Vec Un = n.scaleV(U.dot(n));
		Vec i = Un.scaleV((1+res)/(a.invMass+b.invMass));
		
		//execute impulse
		a.impulse(i);
		b.impulse(i.scale(-1));
		
//		System.out.println(energy());
		
	}
	
	//calculate total energy
	public double energy(){
		Ball b;
		double s=0;
		for(int i=0;i<balls.size();i++){
			b=balls.get(i);
			s+= 0.5 / b.invMass * b.vel.dot(b.vel); 
		}
		return s;
	}

}
