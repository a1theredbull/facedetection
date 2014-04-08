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
	Coord3d[] points;
	Scatter scatter;
	long start;
	
	public GraphPanel(Coord3d[] points) {
		this.points = points;
		scatter = new Scatter(points);
		scatter.setColor(Color.MAGENTA);
		
		start = new Date().getTime();
	}
	
	public void setPoints(Coord3d[] newPoints) {
		scatter.setData(newPoints);
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
	
	public Chart initChart() {
		Chart chart = AWTChartComponentFactory.chart("newt");
		chart.getScene().add(scatter);
		
		return chart;
	}
}