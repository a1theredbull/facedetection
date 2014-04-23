import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import kmeans.Cluster;
import kmeans.KMeans;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.opencv.core.Rect;


public class GraphPanel {
	ArrayList<Coord3d> points;
	Scatter scatter;
	long start;
	int maxDetectedFaces;
	
	public GraphPanel() {
		points = new ArrayList<Coord3d>();
		start = new Date().getTime();
		maxDetectedFaces = 0;
	}
	
	/*
	 * reset the scatter plot data
	 */
	public void replot() {
		Coord3d[] pointsArr = new Coord3d[points.size()];
		points.toArray(pointsArr);
		scatter.setData(pointsArr);
	}
	
	/*
	 * convert the detected rects(faces) to 3d points
	 */
	public Coord3d[] convertRectsToCoord3d(Rect[] rects) {
		Coord3d[] coordinates = new Coord3d[rects.length];
		
		for(int i = 0; i < rects.length; i++) {
			long now = new Date().getTime();
			double timePassed = (double)(now - start) / (double)1000;
			
			coordinates[i] = new Coord3d(rects[i].x, 
					rects[i].y, (float)timePassed);
		}
		
		return coordinates;
	}
	
	/*
	 * add points to the scatter plot
	 */
	public void addPoints(Coord3d[] newPoints) {
		for(int i = 0; i < newPoints.length; i++) {
			points.add(newPoints[i]);
		}
	}
	
	/*
	 * initialize the scatter plot
	 */
	public Chart initChart() {
		Coord3d[] pointsArr = new Coord3d[points.size()];
		points.toArray(pointsArr);
		
		List<Cluster> clusters = assignClusters(pointsArr);
		if(clusters != null) {
			Color[] colors = new Color[pointsArr.length];
		
			Coord3d[] coloredPoints = new Coord3d[pointsArr.length];
		
			int counter = 0;
			for(int i = 0, size = clusters.size(); i < size; i++) {
				Color color = Color.random();
				for(int j = 0, pSize = clusters.get(i).getPoints().size(); j < pSize; j++) {
					colors[counter] = color;
					coloredPoints[counter] = clusters.get(i).getPoints().get(j);
					counter++;
				}
			}
			
			scatter = new Scatter(coloredPoints, colors);
		} else {
			scatter = new Scatter(pointsArr);
		}
		
		scatter.setWidth(4F);
		
		Chart chart = AWTChartComponentFactory.chart("newt");
		chart.setViewPoint(new Coord3d(0.3, 0.3, 1000));
		chart.getScene().add(scatter);
		
		points = new ArrayList<Coord3d>();
		return chart;
	}
	
	private List<Cluster> assignClusters(Coord3d[] coords) {
		if(maxDetectedFaces < 2) {
			return null;
		}
		
		KMeans kMeans = new KMeans(coords, maxDetectedFaces);
		return kMeans.getPointsClusters();
	}
	
	public void setMaxDetectedFaces(int detectedFaces) {
		if(detectedFaces > maxDetectedFaces) {
			maxDetectedFaces = detectedFaces;
		}
	}
}