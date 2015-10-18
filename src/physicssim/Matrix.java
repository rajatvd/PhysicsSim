package physicssim;

/**
 * Simple class to model a matrix.
 * Mainly used to create rotation matrices.
 * Can multiply 2d and 3d vecs
 * @author rajat
 *
 */
public class Matrix {
	
	double[][] elems;
	
	public Matrix(double[][] eles){
		elems = eles;
	}
	
	/**
	 * Create a rotation matrix of angle theta
	 * @param theta - angle
	 */
	public Matrix(double theta){
		elems = new double[2][2];
		elems[0][0] = Math.cos(theta);
		elems[0][1] = -Math.sin(theta);
		elems[1][0] = Math.sin(theta);
		elems[1][1] = Math.cos(theta);
	}
	
	/**
	 * Scale all elements of the matrix by a factor f
	 * @param f - factor
	 */
	public void scale(double f){
		for(int i=0;i<elems.length;i++){
			for(int j=0;i<elems[0].length;j++){
				elems[i][j]*=f;
			}
		}
	}
	
	/**
	 * Multiply the first 2x2 part of the matrix with the 2 elements of Vec v
	 * @param v - Vec
	 * @return A Vec which is the result of the multiplication
	 */
	public Vec multiply2D(Vec v){
		return new Vec(elems[0][0]*v.x+elems[0][1]*v.y,
					   elems[1][0]*v.x+elems[1][1]*v.y);
	}
	
	/**
	 * Multiply the first 3x3 part of the matrix with the 2 elements of Vec v
	 * @param v - Vec
	 * @return A Vec which is the result of the multiplication
	 */
	public Vec multiply3D(Vec v){
		return new Vec(elems[0][0]*v.x+elems[0][1]*v.y+elems[0][2]*v.z,
					   elems[1][0]*v.x+elems[1][1]*v.y+elems[1][2]*v.z,
					   elems[2][0]*v.x+elems[2][1]*v.y+elems[2][2]*v.z);
	}
	
}
