package physicssim;

import java.awt.Color;
import java.util.Vector;

import myio.FileProcessor;

public class State {
	
	//vector of rigid bodies
//	private Vector<RigidBody> Ball.allBalls;
	private FileProcessor fp = new FileProcessor();
	
	private String nl = "\n";
	
	//state information about camera view
	public Vec pan = new Vec();
	public double zoom = 1, grav=1, res=1;
	public Color bgColor = Color.black;
	public boolean walls = false;
	
	//constructors:
	public State(){
//		Ball.allBalls = new Vector<RigidBody>();
	}
	public State(Vector<RigidBody> v){
		Ball.allBalls = v;
	}
	
	//getter and setter
	public Vector<RigidBody> getBodies(){
		return Ball.allBalls;
	}
	public void setBodies(Vector<RigidBody> v){
		Ball.allBalls = v;
	}
	
	/*
	 * Sets the bodies vector by reading a .state text file.
	 * 
	 * The .state file format:
	 * "true" or "false" indicating presence of walls
	 * <Pan Vector>
	 * <Zoom double>
	 * <Grav double>
	 * <Res double>
	 * <BGColor Vector>
	 * <For each body:>
	 * 	"NEXT BODY"
	 * 	<Type of RigidBody>
	 * 	<Position Vector>
	 * 	<Velocity Vector>
	 * 	<Inverse Mass double>
	 * 	<Color Vector>
	 * 	<Body specific parameters>
	 * 
	 */
	/**
	 * Loads a new state from a file
	 */
	public boolean loadState(){
		if(!fp.setFile(null, ".state", false))return false;
		String[] bodyinfo = fp.readFile("NEXT BODY");
		
		String[] init = bodyinfo[0].trim().split("\n");
		walls = init[0].equals("true");
		pan = new Vec(init[1]);
		zoom = doublify(init[2]);
		grav = doublify(init[3]);
		res = doublify(init[4]);
		Vec bg = new Vec(init[5]);
		bgColor = new Color((int)bg.x,
							(int)bg.y,
							(int)bg.z);
		
		Ball.allBalls.clear();
		for(int i=1;i<bodyinfo.length;i++){
			Ball.allBalls.add(readInfo(bodyinfo[i]));		
		}
		return true;
	}
	
	/**
	 * Saves the current state into a file
	 */
	public void saveState(){
		if(!fp.setFile(null, ".state", true))return;
		
		String info="", buffer="";
		//buffer holds the string for a particular body
		
		//camera view state information
		Vec bg = new Vec(bgColor.getRed(),
							  bgColor.getGreen(),
							  bgColor.getBlue());
		info += walls+nl;
		info += pan+nl;
		info += zoom+nl;
		info += grav+nl;
		info += res+nl;
		info += bg+nl;
		
		//rigidbody state information
		for(int i=0;i<Ball.allBalls.size();i++){
			buffer="";
			RigidBody a = Ball.allBalls.get(i);
			
			//delimiter
			info += "NEXT BODY"+nl;
			
			//generic rigidbody information
			Vec color = new Vec(a.c.getRed(),
								a.c.getGreen(),
								a.c.getBlue());
			buffer += a.pos+nl;
			buffer += a.vel+nl;
			buffer += a.invMass+nl;
			buffer += color+nl;
			
			//type specific information
			if(a instanceof Ball){
				buffer += ((Ball) a).r+nl;
				buffer = "Ball"+nl+buffer;
			}
			
			info+=buffer;
		}
		fp.writeFile(info);
	}
	
	/**
	 * Reads Info from a .state file
	 * @param s - String with certain format with details about a RigidBody
	 * @return A RigidBody with properties from the given String
	 */
	private RigidBody readInfo(String s){
		
		//setup
		String type = "";
		String[] info = s.trim().split("\n");
		
		//first line of is the type of rigidbody
		type = info[0];
		
		//parameters irrespective of type of rigidbody read
		RigidBody a = new RigidBody();
		a.pos = new Vec(info[1]);
		a.vel = new Vec(info[2]);
		a.invMass = doublify(info[3]);
		Vec color = new Vec(info[4]);
		a.c = new Color((int)color.x,
						(int)color.y,
						(int)color.z);
		
		//type of rigidbody indentified and specific parameters read
		if(type.equals("Ball")){
			Ball b = new Ball(a);
			b.r = doublify(info[5]);
			return b;
		}
		
		return null;
	}
	
	/**
	 * Short hand for Double.parseDouble
	 * @param s
	 * @return Double form of string s
	 * @throws NumberFormatException
	 */
	private double doublify(String s) throws NumberFormatException{
		return Double.parseDouble(s);
	}
	
}
