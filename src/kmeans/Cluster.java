package kmeans;

import java.util.*;

public class Cluster {

	private final List<Point> points;
	private Point centroid;
	
	public Cluster(Point firstPoint) {
		points = new ArrayList<Point>();
		centroid = firstPoint;
	}
	
	public Point getCentroid(){
		return centroid;
	}
	
	public void updateCentroid(){
		float newx = 0, newy = 0, newz = 0;
		for (Point point : points){
			newx += point.x; newy += point.y; newz += point.z;
		}
		centroid = new Point(newx / points.size(), newy / points.size(), newz / points.size());
	}
	
	public List<Point> getPoints() {
		return points;
	}
	
	public String toString(){
		StringBuilder builder = new StringBuilder("This cluster contains the following points:\n");
		for (Point point : points)
			builder.append(point.toString() + ",\n");
		return builder.deleteCharAt(builder.length() - 2).toString();	
	}
}
