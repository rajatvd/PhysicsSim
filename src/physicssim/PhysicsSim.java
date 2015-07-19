package physicssim;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;

import mousehandler.MouseState;
import mousehandler.MouseStateHandler;
import myio.FileProcessor;

public class PhysicsSim implements ChangeListener{
	
	private static final String VERSION = "v1.5.2";
	
	//GUI fields
	Image img;
	Graphics2D g2;
	JPanel main, image, ballPreview;
	JFrame jf;
	
	//ball collection
//	Vector<RigidBody> Ball.allBalls = new Vector<RigidBody>();
	
	//state object
	State state = new State();
	
	//pan amount and zooming scale
	Vec pan = new Vec();
	double zoom=1;
	
	//coefficient of restitution, gravity contant, ball creation mass
	double res=1, grav=5, mass=1;
	
	//magnitude of normal momentum transfer to walls per timestep, 
	//factor to scale drawn vector length by, to obtain velocity
	double wallMomentum = 0, sensitivity = 0.03;
	
	//wall width and height, ball creation radius
	int wallx=1100, wally=650, radius=6,
		imagex = 2300,imagey = 1200,
		crosshairW = 20, crosshairH = 20;
//		counter=0;
	
//	long startTime,endTime;
	
	//thread delay
	final int DELAY = 10;
	
	//sliders
	//3rd argument is default value
	JSlider radiusS  = new JSlider(JSlider.HORIZONTAL, 0, 50, 6),
			trail   = new JSlider(JSlider.HORIZONTAL, 0, 255, 0),
			restiSlid   = new JSlider(JSlider.HORIZONTAL, 0, 100, 100);
	
	//labels
	JLabel  massLab    = new JLabel("Mass:"),
			radiusLab  = new JLabel("Radius"),
			gravityLab = new JLabel("Gravity Strength:"),
			restiLab   = new JLabel("Restitution:"),
			trailLab   = new JLabel("Trail"),
			energyLab  = new JLabel("Energy: ");
	
	JCheckBox wallsCB   = new JCheckBox("Walls"),
			  momentumCB = new JCheckBox("Show Momentum Vector");
	
	JSpinner gravitySpinner, massSpinner, restiSpinner;
	
	JButton chooseColor = new JButton("Choose ball colour"),
			chooseBGColor = new JButton("Choose background colour"),
			playPause = new JButton("Pause"),
			reset = new JButton("Reset"),
			centerView = new JButton("Center View"),
			lockCamera = new JButton("Lock Camera");

	
	Color ballColor = Color.white,
		  vecColor = Color.yellow,
		  bgColor = new Color(0,0,0,255), 
		  rawBG = Color.black,//backgournd color without transparency
		  crosshairColor = Color.magenta;
	
	boolean drawMomentum = false,
			walls = false,
			running = true;//is not paused?
	
	//Menu fields
	JMenuBar menubar = new JMenuBar();
	
	JMenu file = new JMenu("File");
	
	JMenuItem saveState = new JMenuItem("Save state"),
			  loadState = new JMenuItem("Load state"),
			  saveImage = new JMenuItem("Save image");
	
	Thread thread;
	
	MouseStateHandler mouseHand = new MouseStateHandler();
	MousePan panState = new MousePan();
	MouseVecCreator vecState;
	MouseReleaseLocator ballTracker = new MouseReleaseLocator();
	
	Ball trackedBall;
	
	
	public static void main(String[] args) {
		new PhysicsSim();
	}
	
	@SuppressWarnings("serial")
	public PhysicsSim(){
		
		//jframe
		jf = new JFrame("Physics Simulation "+VERSION);
//		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
		jf.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				exit();
			}
		});
		
		//canvas image
		img = jf.createImage(imagex, imagey);
		g2 = (Graphics2D)img.getGraphics();
		
		
		//Panels
		//simulation canvas
		image = new JPanel(){
			public void paintComponent(Graphics g){
				updatePan();
				clearImage(g2);
				transform(g2);
				drawBodies(g2);
				g.drawImage(img, 0, 0, null);
				if(walls){
					Graphics2D gg = (Graphics2D)g;
					transform(gg);
					drawWalls(gg);
					invTransform(gg);
				}
//				vecState.setGraphics(gg);
				mouseHand.drawStates(g);
				drawVecs(g);
				if(trackedBall!=null)drawCrosshair(g);
				invTransform(g2);
			}
		};
		
		//main container
		main = new JPanel();
		GroupLayout gl = new GroupLayout(main);
		main.setLayout(gl);
		
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
		
		
		//checkboxes
		wallsCB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				walls = wallsCB.isSelected();
				jf.repaint();
				updateState();
			}
		});
		
		momentumCB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				drawMomentum = momentumCB.isSelected();
				jf.repaint();
			}
		});
		
		
		//spinners
		//first argument of constructor is default value
		gravitySpinner = new JSpinner(new SpinnerNumberModel(5,-1000,1000,1.0));
		gravitySpinner.setPreferredSize(new Dimension(70,20));
		gravitySpinner.addChangeListener(this);
		
		massSpinner = new JSpinner(new SpinnerNumberModel(1,1,1000,5.0));
		massSpinner.setPreferredSize(new Dimension(70,20));
		massSpinner.addChangeListener(this);
		
		restiSpinner = new JSpinner(new SpinnerNumberModel(1,0,1,0.01));
		restiSpinner.setPreferredSize(new Dimension(70,20));
		restiSpinner.addChangeListener(this);
		
		//sliders
		trail.setMajorTickSpacing(50);
		trail.setPaintTicks(true);
		trail.setPaintLabels(true);
		trail.addChangeListener(this);
		
		radiusS.setMajorTickSpacing(10);
		radiusS.setPaintTicks(true);
		radiusS.setPaintLabels(true);
		radiusS.addChangeListener(this);
		
		restiSlid.setMajorTickSpacing(20);
		restiSlid.setPaintTicks(true);
		restiSlid.setPaintLabels(true);
		restiSlid.addChangeListener(this);
		
		
		//buttons
		chooseColor.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				Color c = JColorChooser.showDialog(jf, "Choose ball colour", ballColor);
				if(c==null)return;
				ballColor = c;
				jf.repaint();
			}		
		});
		chooseBGColor.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				Color c = JColorChooser.showDialog(jf, "Choose ball colour", ballColor);
				if(c==null)return;
				bgColor = new Color(c.getRed(),c.getGreen(),c.getBlue(),255-trail.getValue());
				rawBG = c;
				updateState();
				jf.repaint();
			}		
		});
		reset.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				Ball.allBalls.clear();
				zoom=1;
				centreView();
				trackedBall = null;
			}		
		});
		centerView.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				centreView();
			}		
		});
		playPause.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if(playPause.getText().equals("Pause")){
					running = false;
					playPause.setText("Play");
				}else if(playPause.getText().equals("Play")){
					running = true;
					playPause.setText("Pause");
				}
			}		
		});
		lockCamera.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if(lockCamera.getText().equals("Lock Camera")){
					mouseHand.setAllState(ballTracker);
					mouseHand.setLeftState(MouseState.doNothing);
					lockCamera.setText("Create ball");
				}else{
					mouseHand.setLeftState(vecState);
					mouseHand.setAllState(MouseState.doNothing);
					lockCamera.setText("Lock Camera");
				}
			}
		});
		
		
		//Menu
		
		//adding the menus
		menubar.add(file);
		
		//setting menubar of jf
		jf.setJMenuBar(menubar);
		
		//adding menu items to the menus
		file.add(saveState);
		file.add(loadState);
		file.add(saveImage);
		
		//event handling for menu items
		saveState.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				boolean temp = running;
				running = false;
				updateState();
				state.saveState();
				running = temp;
				jf.repaint();
			}		
		});
		loadState.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				boolean temp = running;
				running = false;
				if(!state.loadState()){
					running = temp;
					return;
				}
				trackedBall = null;
				putState();
				running = temp;
				jf.repaint();
			}		
		});
		saveImage.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				boolean temp = running;
				running = false;
				//saving part of the image which is visible
				BufferedImage bf = new BufferedImage(image.getWidth(),
											image.getHeight(),
											BufferedImage.TYPE_4BYTE_ABGR);
				bf.getGraphics().drawImage(img, 0, 0,null);
				bf.getGraphics().dispose();
				FileProcessor fp = new FileProcessor();
				if(!fp.setFile(jf, ".png", true)){
					running = temp;
					bf.flush();
					return;
				}
				try {
					ImageIO.write(bf, "png", fp.getFile());
				} catch (IOException e) {
					e.printStackTrace();
				}
				running = temp;
				bf.flush();
			}		
		});
		
		
		//Layout
		gl.setHorizontalGroup(
				gl.createSequentialGroup()
				.addComponent(image,
						0, 
						GroupLayout.DEFAULT_SIZE,
				        Short.MAX_VALUE)//main jpanel with image 
				        
				//parallel group of parameter tuners:
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.CENTER, false)
						.addGroup(gl.createSequentialGroup()
								.addComponent(reset)
								.addComponent(centerView)
								)
						.addGroup(gl.createSequentialGroup()
								.addComponent(gravityLab)
								.addComponent(gravitySpinner,
										GroupLayout.PREFERRED_SIZE, 
										GroupLayout.PREFERRED_SIZE,
								        GroupLayout.PREFERRED_SIZE)
								)
						.addGroup(gl.createSequentialGroup()
								.addComponent(restiLab)
								.addComponent(restiSpinner,
										GroupLayout.PREFERRED_SIZE, 
										GroupLayout.PREFERRED_SIZE,
								        GroupLayout.PREFERRED_SIZE)
								)
						.addComponent(restiSlid)
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
						.addComponent(playPause)
						.addComponent(lockCamera)
						)
				);
		
		gl.setVerticalGroup(
				gl.createParallelGroup()
				.addComponent(image,
						0, 
						GroupLayout.DEFAULT_SIZE,
				        Short.MAX_VALUE)
				.addGroup(gl.createSequentialGroup()
						.addGroup(gl.createParallelGroup()
								.addComponent(reset)
								.addComponent(centerView)
								)
						.addGroup(gl.createParallelGroup()
								.addComponent(gravityLab)
								.addComponent(gravitySpinner,
										GroupLayout.PREFERRED_SIZE, 
										GroupLayout.PREFERRED_SIZE,
								        GroupLayout.PREFERRED_SIZE)
								)
						.addGroup(gl.createParallelGroup()
								.addComponent(restiLab)
								.addComponent(restiSpinner,
										GroupLayout.PREFERRED_SIZE, 
										GroupLayout.PREFERRED_SIZE,
								        GroupLayout.PREFERRED_SIZE)
								)
						.addComponent(restiSlid)
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
						.addComponent(playPause)
						.addComponent(lockCamera)
						)
				);
		gl.setAutoCreateGaps(true);
		gl.setAutoCreateContainerGaps(true);
		
		
		
		//mouse response
		vecState = new MouseVecCreator();
		panState.setPanVec(pan);
		ballTracker.color = crosshairColor;
		ballTracker.w = crosshairW;
		ballTracker.h = crosshairH;
		
//		mouseHand.setLeftState(ballTracker);
		mouseHand.setLeftState(vecState);
		mouseHand.setRightState(panState);
		
		MouseAdapter mouse = new MouseAdapter(){
			
			public void mouseClicked(MouseEvent e){
				mouseHand.clickAction(e);
			}
			
			public void mousePressed(MouseEvent e){
				mouseHand.pressAction(e);
				if(mouseHand.getRightState().equals(panState) 
						&& SwingUtilities.isRightMouseButton(e)){
					trackedBall = null;
				}
			}
			
			public void mouseDragged(MouseEvent e){
				mouseHand.dragAction(e);
				jf.repaint();
			}
			
			public void mouseReleased(MouseEvent e){
				mouseHand.releaseAction(e);
				
				if(vecState.hasVec()){
					Vec vel = vecState.getVec().scale(sensitivity/zoom);
					Vec pos = invTransform(vecState.getOrigin());
//					Ball.allBalls.add(new Ball(pos, 
//							vel,
//							radius, 
//							mass,
//							ballColor));
					new Ball(pos, 
							vel,
							radius, 
							mass,
							ballColor);
				}
				if(ballTracker.hasVec()){
					trackBall(ballTracker.getVec());
				}
				
				updateState();
				jf.repaint();
			}
			
			public void mouseMoved(MouseEvent e){
				mouseHand.moveAction(e);
				if(mouseHand.getAllState()!=null && mouseHand.getAllState().equals(ballTracker)){
					jf.repaint();
				}
			}
			
			public void mouseWheelMoved(MouseWheelEvent e){
				double newZoom = zoom*(1-e.getPreciseWheelRotation()/10);
				//mousePos is the position vector of the mouse with respect to zooming origin(pan).
				//Dividing by zoom yields the actual position vector of an imaginary object
				//as if it were at the mouse. Multiplying by newZoom gets the new position vector
				//with respect to zooming origin. The change in position of the object is
				//mousePos*(newZoom/zoom) - mousePos. Subtracting this from pan causes the object to
				//be unmoved.
//				Vec mousePos = new Vec(e.getX(),e.getY()).minus(pan);
//				pan.subtract(mousePos.scaleV(newZoom/zoom).minus(mousePos));
//				zoom=newZoom;
//				jf.repaint();
//				updateState();
				zoom(newZoom, new Vec(e.getX(),e.getY()));
			}
		};
		
		image.addMouseListener(mouse);
		image.addMouseMotionListener(mouse);
		image.addMouseWheelListener(mouse);
		
		//initialising the state object
		updateState();
		
		//simulation thread
//		thread = new Thread(new Runnable(){
//			public void run(){
//				while(true){
//					try {
//						Thread.sleep(DELAY);
//						if(!running)continue;
//						update();
////						jf.repaint();
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					} catch (ArrayIndexOutOfBoundsException e) {
//						e.printStackTrace();
//						bodies.clear();
//					}
//				}
//			}
//		});
		
		
		jf.setContentPane(main);
		jf.setSize(wallx+240,wally+70);
		
//		thread.start();
		
		while(true){
			try {
				Thread.sleep(DELAY);
				if(!running)continue;
//				startTime = System.nanoTime();
				update();
//				endTime = System.nanoTime();
//				System.out.println((endTime-startTime)/1e6);
//				while(updating)continue;
//				startTime = System.nanoTime();
				jf.repaint();
//				endTime = System.nanoTime();
//				System.out.println((endTime-startTime)/1e6);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
				Ball.allBalls.clear();
			}
		}
		
	}
	
	/**
	 * Tracks the first ball found which contains the point given by Vec v.
	 * If no such ball is found, no tracking is done.
	 * @param v - Defines the point which is used to find a ball to track
	 */
	public void trackBall(Vec v) {
		trackedBall = null;
		for(int i=0;i<Ball.allBalls.size();i++){
			Ball a = (Ball) Ball.allBalls.elementAt(i);
			Vec c = transform(a.pos);
			if(v.minus(c).mag()<a.r*zoom){
				System.out.println(a.r*zoom);
				trackedBall = a;
				return;
			}
		}
		System.out.println(trackedBall);
	}

	/**
	 * Show a message dialog and exit from the program.
	 */
	public void exit() {
		String msg = "Physics Simulation "+VERSION+" made by Rajat V D\n" +
				"2015";
		JOptionPane.showMessageDialog(jf, msg, "Exiting...", JOptionPane.PLAIN_MESSAGE);
		System.exit(0);
	}

	//implementation for change listener
	public void stateChanged(ChangeEvent e) {
		Object source = e.getSource();
		if(source instanceof JSlider){
			JSlider jsl = (JSlider)e.getSource();
			if(jsl.equals(trail)){
				bgColor = new Color(rawBG.getRed(),
									rawBG.getGreen(),
									rawBG.getBlue(),
									255-trail.getValue());
			}else if(jsl.equals(radiusS)){
				radius = radiusS.getValue();
			}
			else if(jsl.equals(restiSlid)){
				res = restiSlid.getValue()/100.0;
				restiSpinner.setValue(res);
			}
		}else if(source instanceof JSpinner){
			JSpinner js = (JSpinner)source;
			if(js.equals(gravitySpinner)){
				grav = (Double) gravitySpinner.getValue();
			}else if(js.equals(massSpinner)){
				mass = (Double) massSpinner.getValue();
			}else if(js.equals(restiSpinner)){
				res = (Double) restiSpinner.getValue();
				restiSlid.setValue((int) (res*100));
			}
		}
		jf.repaint();
	}
	
	
	//Graphics functions:
	

	public void updatePan() {
		Vec centre = new Vec(image.getWidth()/2,image.getHeight()/2);
		if(trackedBall!=null){
			pan.set(trackedBall.pos.scaleV(-zoom).plus(centre));
		}
		
	}
	
	/**
	 * Perform all the functions when the zoom changes
	 * @param newZoom - new zoom amount
	 * @param centre - the centre of the zooming(in the apparent view)
	 */
	public void zoom(double newZoom, Vec centre){
		//centre is the position vector of zooming origin. Subtracting the pan away yields the position
		//vector of an imaginary object at the zooming origin.
		//Dividing by zoom yields the actual position vector of an imaginary object
		//as if it were at the mouse. Multiplying by newZoom gets the new apparent position vector
		//with respect to zooming origin. The change in position of the object is
		//centre*(newZoom/zoom) - centre. Subtracting this from pan causes the object to
		//be unmoved.
		
		//centre is now apparent position vector of an imaginary object at the zoom origin
		centre.subtract(pan);
		
		//(centre*(newZoom/zoom) - centre) + (change in pan) = 
		//net change in apparent position of imaginary object.
		//for the object to be unmoved, net change = 0,
		//change in pan = -(centre*(newZoom/zoom) - centre)
		pan.subtract(centre.scaleV(newZoom/zoom).minus(centre));
		
		zoom=newZoom;
		jf.repaint();
		updateState();
	}
	
	public void drawCrosshair(Graphics g){
		Vec centre = new Vec(image.getWidth()/2,image.getHeight()/2);
		g.setColor(crosshairColor);
		int w = (int)Math.max((crosshairW/2+2*trackedBall.r)*zoom, crosshairW),
			h = (int)Math.max((crosshairH/2+2*trackedBall.r)*zoom, crosshairH);
		g.drawOval((int)(centre.x-w/2), 
				(int)(centre.y-h/2), 
				w, 
				h);
	}
	
	public void centreView(){
		if(Ball.allBalls.size()==0){
			pan.x=image.getWidth()/2 - wallx*zoom/2;
			pan.y=image.getHeight()/2 - wally*zoom/2;
		}else{
			Vec c = (invTransform(centerOM()).scale(-1*zoom))
					.plus(new Vec(image.getWidth()/2, image.getHeight()/2));
			pan.set(c);
		}
		jf.repaint();
	}
	
	public Color invert(Color c){
		return new Color(255-c.getRed(), 255-c.getGreen(), 255-c.getBlue());
	}
	
	public void clearImage(Graphics g){
		g.setColor(bgColor);
		g.fillRect(0, 0, imagex, imagey);
	}
	
	public void drawWalls(Graphics g){
		g.setColor(invert(rawBG));
		g.drawRect(0, 0, wallx, wally);
	}
	
	/**
	 * Draw all bodies
	 * @param g - Graphics
	 */
	public void drawBodies(Graphics g){	
		for(int i=0;i<Ball.allBalls.size();i++){
			Ball.allBalls.elementAt(i).drawBody(g);
		}		
	}
		
		
	/**
	 * Draw all vectors
	 * @param g - Graphics
	 */
	public void drawVecs(Graphics g){
		if(drawMomentum)drawMomentum(g);
	}
	
	
	/**
	 * To draw total momentum vector from the center of mass
	 * @param g - Graphics to draw with
	 */
	public void drawMomentum(Graphics g){
		RigidBody b;
		Vec s = new Vec();
		for(int i=0;i<Ball.allBalls.size();i++){
			b=Ball.allBalls.get(i);
			s.add(b.vel.scaleV(1/b.invMass)); 
		}
		s.scale(zoom/sensitivity).draw(g, centerOM(), vecColor);
	}
	
	/**
	 * 
	 * @return The center of mass of the collection of balls
	 */
	public Vec centerOM(){
		Vec s = new Vec(0,0);
		double m=0;
		RigidBody b;
		for(int i=0;i<Ball.allBalls.size();i++){
			b=Ball.allBalls.get(i);
			s.add(b.pos.scaleV(1/b.invMass));
			m+=1/b.invMass;
		}
		return transform(s.scale(1/m));
	}
	
	
	//Zoom and pan transforms of graphics2D
	/**
	 * Translate then scale
	 * @param g - Graphics2D to transform
	 */
	public void transform(Graphics2D g){
		g.translate(pan.x, pan.y);
		g.scale(zoom, zoom);
	}
	
	/**
	 * Inverse scale then inverse translate
	 * @param g - Graphics2D to transform
	 */
	public void invTransform(Graphics2D g){
		g.scale(1/zoom, 1/zoom);	
		g.translate(-pan.x, -pan.y);
	}
	
	//Zoom and pan transforms of vectors
	/**
	 * Inverse scale then inverse translate
	 * @param v - Vec to transform
	 */
	public Vec transform(Vec v){
		return v.scaleV(zoom).plus(pan.x, pan.y);
	}
	
	/**
	 * Inverse translate then inverse scale
	 * @param v - Vec to transform
	 */
	public Vec invTransform(Vec v){
		return v.plus(-pan.x, -pan.y).scaleV(1/zoom);
	}
	
	
	//State managment:
	
	/**
	 * Updates the information in the state object to match the current information
	 */
	public void updateState(){
		state.setBodies(Ball.allBalls);
		state.walls = walls;
		state.pan.set(pan);
		state.zoom = zoom;
		state.grav = grav;
		state.res = res;
		state.bgColor = rawBG;
	}
	
	/**
	 * Updates the current information to match that in the state
	 */
	public void putState(){
		walls = state.walls;
		wallsCB.setSelected(walls);
		pan.set(state.pan);
		zoom = state.zoom;
		grav = state.grav;
		gravitySpinner.setValue(grav);
		res = state.res;
		restiSpinner.setValue(res);
		Color c = state.bgColor;
		bgColor = new Color(c.getRed(),c.getGreen(),c.getBlue(),255-trail.getValue());
		rawBG = c;
		energyLab.setText(String.format("Kinetic Energy: %.8g", kineticEnergy()));
	}
	
	
	//Physics functions:
	
	/**
	 * Main update function
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public void update() throws ArrayIndexOutOfBoundsException{
		wallMomentum = 0;
		Ball a,b;
//		for(int i=0;i<bodies.size();i++){
//			a = (Ball) bodies.elementAt(i);
//			a.newAcc = new Vec(Vec.ZERO);
//			a.acc = new Vec(Vec.ZERO);
//		}
		for(int i=0;i<Ball.allBalls.size();i++){
			a = (Ball) Ball.allBalls.elementAt(i);
			for(int j=0;j<Ball.allBalls.size();j++){
				if(i==j)continue;
				b = (Ball) Ball.allBalls.elementAt(j);
				//gravity
//				if(j>i)gravitate(a,b);
				if(checkCollision(a,b)){
					collide(a,b,res);
				}
			}
			if(walls)checkWall(a);
			a.verletUpdate(grav);
//			Ball.allBalls.elementAt(i).velUpdate();
//			Ball.allBalls.elementAt(i).update();
		}
//		updatePan();
//		System.out.println(wallMomentum!=0?wallMomentum:"");
		energyLab.setText(String.format("Kinetic Energy: %.8g", kineticEnergy()));
	}

	public boolean checkCollision(Ball a, Ball b){
		return a.pos.minus(b.pos).mag()<a.r+b.r;
	}
	
	/**
	 * Check and perform wall collision
	 * @param a - The ball to check
	 */
	public void checkWall(Ball a){
		if(a.pos.x<=a.r){
			a.vel.x = -a.vel.x;
			a.pos.x=a.r;
			wallMomentum += Math.abs(a.vel.x)/a.invMass;
		}
		if(a.pos.x>=wallx-a.r){
			a.vel.x = -a.vel.x;
			a.pos.x=wallx-a.r;
			wallMomentum += Math.abs(a.vel.x)/a.invMass;
		}
		if(a.pos.y<=a.r){
			a.vel.y = -a.vel.y;
			a.pos.y=a.r;
			wallMomentum += Math.abs(a.vel.y)/a.invMass;
		}
		if(a.pos.y>=wally-a.r){
			a.vel.y = -a.vel.y;
			a.pos.y=wally-a.r;
			wallMomentum += Math.abs(a.vel.y)/a.invMass;
		}
	}
	
	/**
	 * Perform gravity update
	 * @param a - first ball to gravitate
	 * @param b - second ball to gravitate
	 */
	public void gravitate(Ball a, Ball b){
		Vec r = b.pos.minus(a.pos);
		//to prevent penetration gravity
		if(r.mag()<a.r+b.r)return;
		
		Vec i = r.scaleV(grav/(Math.pow(r.mag(),3)*b.invMass*a.invMass));
		
//		a.vel.add(r.scaleV(grav/(Math.pow(r.mag(),3)*b.invMass)));
//		b.vel.add(r.scaleV(-grav/(Math.pow(r.mag(),3)*a.invMass)));
		
//		a.setAcc(a.newAcc.plus(i.scaleV(a.invMass)));
//		b.setAcc(b.newAcc.plus(i.scaleV(-b.invMass)));
		
		a.impulse(i);
		b.impulse(i.scaleV(-1));
	}
	
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
	
	/**
	 * Collides a and b
	 * @param a - first ball
	 * @param b - second ball
	 * @param rest - coefficient of restitution for the collision
	 */
	public void collide(Ball a, Ball b, double rest){
		//get vectors
		Vec ua = a.vel, ub = b.vel;
		Vec U = ub.minus(ua);
		Vec n = b.pos.minus(a.pos);
		if(n.mag()==0)return;
		n.scale(1/n.mag());
		
		//check if collision is proper
		if(U.dot(n)>=0){
			separate(a,b);
			return;
		}
		
		
		//find impulse
		Vec Un = n.scaleV(U.dot(n));
		Vec i = Un.scaleV((1+res)/(a.invMass+b.invMass));
		
		//execute impulse
		a.impulse(i);
		b.impulse(i.scale(-1));
		
//		System.out.println(energy());
		
	}
	
	/**
	 * Calculate kinetic energy
	 * @return Kinetic energy of the system
	 */
	public double kineticEnergy(){
		RigidBody b;
		double s=0;
		for(int i=0;i<Ball.allBalls.size();i++){
			b=Ball.allBalls.get(i);
			s+= 0.5 / b.invMass * b.vel.dot(b.vel); 
		}
		return s;
	}
	
	/**
	 * Calculate potential energy
	 * @return Potential energy of the system
	 */
	public double potentialEnergy(){
		RigidBody a,b;
		double s=0;
		for(int i=0;i<Ball.allBalls.size();i++){
			a=Ball.allBalls.get(i);
			for(int j=i+1;j<Ball.allBalls.size();j++){
				b=Ball.allBalls.get(j);
				s += -1*grav/(a.invMass*b.invMass*a.pos.minus(b.pos).mag());
			}
		}
		return s;
	}
	
	/**
	 * Separates two balls if they are colliding. It moves both balls away from each other
	 * an equal distance along the line joining their centres, until they do not intersect.
	 * @param a - First Ball
	 * @param b - Second Ball
	 */
	public void separate(Ball a, Ball b){
		while(checkCollision(a,b)){
			Vec r = b.pos.minus(a.pos);
			r.scale(0.01/r.mag());
			a.pos.subtract(r);
			b.pos.add(r);
		}
	}
	
}
