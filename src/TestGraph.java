import java.awt.Panel;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot2d.rendering.Canvas;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.rendering.canvas.CanvasNewtAwt;

public class TestGraph {
	public static void main(String[] args) {
		plot("newt");
	}

	public static void plot(String canvasType) {
		// Create a test chart.
		int size = 1000;
		Coord3d[] points = new Coord3d[size];
		for (int i=0; i<size; i++) {
		    points[i] = new Coord3d(
			    (float) Math.random() - 0.5f, 
			    (float) Math.random() - 0.5f, 
			    (float) Math.random() - 0.5f);	    
		}	
		Scatter scatter = new Scatter(points);
		scatter.setColor(Color.BLUE);	
		Chart chart = AWTChartComponentFactory.chart("newt");
		chart.getScene().add(scatter);
			
		// Embed into Swing.
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		panel.add((CanvasNewtAwt)chart.getCanvas());
		frame.setContentPane(panel);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setTitle(canvasType);
		frame.setVisible(true);
	}
	
}
