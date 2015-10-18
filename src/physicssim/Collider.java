package physicssim;

public class Collider {
	
	
	/**
	 * Checks if any of the corners of rectangle 'b' intersect with rectangle 'a'.
	 * @param a - First rectangle
	 * @param b - Second rectangle
	 * @return Array of 2 vectors. First vector is the position of collision,
	 * and second is the unit normal vector of the collision.
	 */
	public Vec[] checkCollision(Rect a, Rect b){
		
		Vec[] vecs = new Vec[2];
		
		Vec apos = a.pos;
		Vec[] corners = b.getCorners();
		Vec v = new Vec();
		Matrix mat = new Matrix(-a.angPos);
		//find collision point:
		for(int i=0;i<4;i++){
			//b corner vector relative to centre of a, then rotate to make 
			//a line up with axes
			v = mat.multiply2D(corners[i].minus(apos));
//			System.out.println(Math.abs(v.x));
			//condition for corner vec to be inside a
			if(Math.abs(v.x)<=a.w/2 && Math.abs(v.y)<=a.h/2){
//				mat = new Matrix(a.angPos);
//				vecs[0] = mat.multiply2D(v).plus(a.pos);
				vecs[0] = v;
				break;
			}
		}
		
		//find collision normal:
		
		//if no collision point, return null Vecs
		if(vecs[0]==null)return vecs;
		
		double[] dists = new double[4];
		
		//perpendicular distances between collision point and sides of a
		dists[0] = a.h/2 - vecs[0].y;
		dists[1] = a.w/2 - vecs[0].x;
		dists[2] = vecs[0].y + a.h/2;
		dists[3] = vecs[0].x + a.w/2;
		
		//find the side closest to collision point
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
		
		//decide normal vector based on closest side found above
		if(mini==0)vecs[1] = new Vec(0,1);
		else if(mini==1)vecs[1] = new Vec(1,0);
		else if(mini==2)vecs[1] = new Vec(0,-1);
		else if(mini==3)vecs[1] = new Vec(-1,0);
		
		//undo initial rotation to return the result in original frame
		mat = new Matrix(a.angPos);
		vecs[0] = mat.multiply2D(vecs[0]).plus(apos);
		vecs[1] = mat.multiply2D(vecs[1]);
		
		//collision point, collision normal returned
		return vecs;
		
	}
	
	
	/**
	 * Calculates and applies the collision response impulse. The impulse vector
	 * is also returned.
	 * 
	 * The order of rectangles is important, b must be the rect whose corner
	 * is intersecting a.
	 * @param a - First rectangle
	 * @param b - Rect whose corner is intersecting into a
	 * @param c - Point of collision or contact
	 * @param nn - Unit normal vector of collision
	 * @param res - Coefficient of restituion of collision
	 * @return Impulse response of collision
	 */
	public Vec collide(Rect a, Rect b, Vec c, Vec nn, double res){
		
		// to find the normal unit vector:
		Vec n = nn.scaleV(1);
//		n=a.getNormals()[2];
		
		
		//collision
		Vec ra = c.minus(a.pos), rb = c.minus(b.pos),
			ua =  a.vel.plus(a.angVel.cross(ra)), ub = b.vel.plus(b.angVel.cross(rb));

		//relative velocity along collision normal at point of contact
		Vec Un = n.scaleV(ub.minus(ua).dot(n));
		
		if(Un.dot(n)>0)return Vec.ZERO;
		
		//to fine impulse
		double denom = a.invMass+b.invMass+(rb.cross(n).cross(rb).scaleV(b.invMOI)
											.plus(ra.cross(n).cross(ra).scaleV(a.invMOI))
											.dot(n));
		//the impulse
		Vec j = Un.scaleV((1+res)/denom);
//		System.out.println(denom);
		
		
		//applying impulses to each rect
		a.impulse(j);
		b.impulse(j.scaleV(-1));
		
		a.angImpulse(j,ra);
		b.angImpulse(j.scaleV(-1),rb);
		
//		System.out.println(ub.minus(ua).mag());
//		System.out.println(j+"\n"+a.angVel+"\n"+b.angVel);
		
		return j;
		
	}
}
