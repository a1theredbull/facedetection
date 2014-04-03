import java.util.Date;

import javax.swing.JComponent;

import org.jzy3d.chart.Chart;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.ScatterMultiColor;
import org.opencv.core.Rect;


public class GraphPanel {
	Coord3d[] points;
	ScatterMultiColor scatter;
	
	public GraphPanel(Coord3d[] points) {
		this.points = points;
		scatter = new ScatterMultiColor(points, 
				new ColorMapper(new ColorMapRainbow(), -0.5f, 0.5f));
	}
	
	public void setPoints(Coord3d[] newPoints) {
		scatter.setData(newPoints);
	}
	
	public Coord3d[] convertRectsToCoord3d(Rect[] rects) {
		Coord3d[] coordinates = new Coord3d[rects.length];
		
		for(int i = 0; i < rects.length; i++) {
			long now = new Date().getTime();
			coordinates[i] = new Coord3d((float)rects[i].x, 
					(float)rects[i].y, (float)now);
		}
		
		return coordinates;
	}
	
	public JComponent initChart() {
		Chart chart = new Chart("swing");
		
		//chart.getAxeLayout().setMainColor(Color.WHITE);
		chart.getView().setBackgroundColor(Color.BLACK);
		chart.getScene().add(scatter);
		
		return (JComponent) chart.getCanvas();
	}
}