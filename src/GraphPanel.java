import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import javax.swing.JComponent;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.primitives.ScatterMultiColor;
import org.jzy3d.plot3d.rendering.canvas.CanvasNewtAwt;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.opencv.core.Rect;


public class GraphPanel {
	ArrayList<Coord3d> points;
	Scatter scatter;
	long start;
	
	public GraphPanel() {
		points = new ArrayList<Coord3d>();
		start = new Date().getTime();
	}
	
	public void replot() {
		Coord3d[] pointsArr = new Coord3d[points.size()];
		points.toArray(pointsArr);
		scatter.setData(pointsArr);
	}
	
	public Coord3d[] convertRectsToCoord3d(Rect[] rects) {
		Coord3d[] coordinates = new Coord3d[rects.length];
		
		for(int i = 0; i < rects.length; i++) {
			long now = new Date().getTime();
			double timePassed = (double)(now - start) / (double)1000;
			
			coordinates[i] = new Coord3d((float)rects[i].x, 
					(float)rects[i].y, (float)timePassed);
		}
		
		return coordinates;
	}
	
	public void addPoints(Coord3d[] newPoints) {
		for(int i = 0; i < newPoints.length; i++) {
			points.add(newPoints[i]);
		}
	}
	
	public Chart initChart() {
		Coord3d[] pointsArr = new Coord3d[points.size()];
		points.toArray(pointsArr);
		
		scatter = new Scatter(pointsArr);
		scatter.setColor(Color.MAGENTA);
		scatter.setWidth(2.5F);
		
		Chart chart = AWTChartComponentFactory.chart("newt");
		chart.setViewPoint(new Coord3d(0.3, 0.3, 1000));
		chart.getScene().add(scatter);
		
		return chart;
	}
}