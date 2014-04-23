package kmeans;

import org.jzy3d.maths.Coord3d;

public class Point extends Coord3d {
	
	private int index = -1;	//denotes which Cluster it belongs to

	public Point(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public float getSquareOfDistance(Point anotherPoint){
		return  (x - anotherPoint.x) * (x - anotherPoint.x)
				+ (y - anotherPoint.y) *  (y - anotherPoint.y)
				+ (z - anotherPoint.z) *  (z - anotherPoint.z);
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
	public String toString(){
		return "(" + x + "," + y + "," + z + ")";
	}	
}
