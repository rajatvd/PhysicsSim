package physicssim;

import java.awt.Color;
import java.util.Vector;

import myio.FileProcessor;

public class State {
	
	//vector of rigid bodies
	private Vector<RigidBody> bodies;
	private FileProcessor fp = new FileProcessor();
	
	//state information about camera view
	public Vec pan = new Vec();
	public double zoom = 1;
	public Color bgColor = Color.black;
	public boolean walls = false;
	
	//constructors:
	public State(){
		bodies = new Vector<RigidBody>();
	}
	public State(Vector<RigidBody> v){
		bodies = v;
	}
	
	//getter and setter
	public Vector<RigidBody> getBodies(){
		return bodies;
	}
	public void setBodies(Vector<RigidBody> v){
		bodies = v;
	}
	
	/*
	 * Sets the bodies vector by reading a .state text file.
	 * 
	 * The .state file format:
	 * "true" or "false" indicating presence of walls
	 * <Pan Vector>
	 * <Zoom double>
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
	public void loadState(){
		if(!fp.setFile(null, ".state", false))return;
		String[] bodyinfo = fp.readFile("NEXT BODY");
		
		String[] init = bodyinfo[0].trim().split("\n");
		walls = init[0].equals("true");
		pan = new Vec(init[1]);
		zoom = doublify(init[2]);
		Vec bg = new Vec(init[3]);
		bgColor = new Color((int)bg.x,
							(int)bg.y,
							(int)bg.z);
		
		bodies.clear();
		for(int i=1;i<bodyinfo.length;i++){
			bodies.add(readInfo(bodyinfo[i]));		
		}
	}
	
	public void saveState(){
		if(!fp.setFile(null, ".state", true))return;
		
		String info="", buffer="";
		//buffer holds the string for a particular body
		
		//camera view state information
		Vec bg = new Vec(bgColor.getRed(),
							  bgColor.getGreen(),
							  bgColor.getBlue());
		info += walls+"\n";
		info += pan+"\n";
		info += zoom+"\n";
		info += bg+"\n";
		
		//rigidbody state information
		for(int i=0;i<bodies.size();i++){
			buffer="";
			RigidBody a = bodies.get(i);
			
			//delimiter
			info += "NEXT BODY\n";
			
			//generic rigidbody information
			Vec color = new Vec(a.c.getRed(),
								a.c.getGreen(),
								a.c.getBlue());
			buffer += a.pos+"\n";
			buffer += a.vel+"\n";
			buffer += a.invMass+"\n";
			buffer += color+"\n";
			
			//type specific information
			if(a instanceof Ball){
				buffer += ((Ball) a).r+"\n";
				buffer = "Ball\n"+buffer;
			}
			
			info+=buffer;
		}
		fp.writeFile(info);
	}
	
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
	
	private double doublify(String s) throws NumberFormatException{
		return Double.parseDouble(s);
	}
	
}
