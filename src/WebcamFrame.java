import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

/*
 * Authors: Alexander Chau, Cameron Ohrt
 * CIS 365 Artificial Intelligence
 * Project 3 - OpenCV
 */

public class WebcamFrame {
	public static void main(String[] args) {
		System.loadLibrary("opencv_java248");
		
		//initialize swing components
		JFrame frame = new JFrame("CIS 365 Detection");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		CVPanel dPanel = new CVPanel();
		BinaryPanel bPanel = new BinaryPanel();
		TrackPanel tPanel = new TrackPanel();
		
		JPanel contentPane = new JPanel(new GridLayout(2, 1));
		contentPane.add(dPanel);
		contentPane.add(bPanel);
		contentPane.add(tPanel);
		
		frame.setContentPane(contentPane);
		
		//initialize face detector engine
		FaceDetector detector = new FaceDetector(
				"C:/opencv/sources/data/haarcascades/haarcascade_mcs_nose.xml");
		
		//initialize webcam capture
		Mat webcamImage = new Mat();
		VideoCapture capture = new VideoCapture(0);
		
		if (capture.isOpened()) {
			while (true) {
				capture.read(webcamImage);
				if (!webcamImage.empty()) {
					Imgproc.resize(webcamImage, webcamImage, new Size(400, 300));
					contentPane.setSize(webcamImage.width() * 2 + 60,
							webcamImage.height() * 2 + 60);
					frame.setSize(contentPane.getSize());
					webcamImage = detector.detectFaces(webcamImage);
					dPanel.matToBufferedImage(webcamImage);
					dPanel.repaint();
					
					bPanel.imageToBinaryScale(webcamImage);
					bPanel.matToBufferedImage(webcamImage);
					bPanel.repaint();
					
					tPanel.setRects(detector.getRects());
					tPanel.paintComponent(tPanel.getGraphics());
				} else {
					System.out.println("Can't find captured frame.");
					break;
				}
			}
		}
		return;
	}
}

//converts each frame to buffered images for analysis
class CVPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private BufferedImage image;
	
	public BufferedImage getImage() {
		return image;
	}
	
	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
	/**
	 * Converts/writes a Mat into a BufferedImage.
	 * 
	 * @param matrix
	 *            Mat of type CV_8UC3 or CV_8UC1
	 * @return BufferedImage of type TYPE_3BYTE_BGR or TYPE_BYTE_GRAY
	 */
	public boolean matToBufferedImage(Mat matBGR) {
		int width = matBGR.width(), height = matBGR.height(), channels = matBGR
				.channels();
		byte[] sourcePixels = new byte[width * height * channels];
		matBGR.get(0, 0, sourcePixels);
		// create new image and get reference to backing data
		image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster()
				.getDataBuffer()).getData();
		System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);
		return true;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (this.image == null)
			return;
		g.drawImage(this.image, 10, 10, this.image.getWidth(),
				this.image.getHeight(), null);
	}
}

class BinaryPanel extends CVPanel {
	private static final long serialVersionUID = 1L;
	
	public boolean imageToBinaryScale(Mat matBGR) {
		Imgproc.GaussianBlur(matBGR, matBGR, new Size(3,3), 4);
		Imgproc.threshold(matBGR, matBGR, 30, 255, 
				Imgproc.THRESH_BINARY_INV);
		
		return true;
	}
}

class TrackPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private ArrayList<ColoredRect> coloredRects = new ArrayList<ColoredRect>();
	private ArrayList<Rect> rects;
	
	//number of times a face must be detected before confirmation
	private int comboConfirmation = 0;
	
	public void setRects(Rect[] rects) {
		this.rects = new ArrayList<Rect>(Arrays.asList(rects));
	}
	
	public void paintComponent(Graphics g) {		
		if(rects == null) return;
		
		for(int i = 0, size = rects.size(); i < size; i++) {
			Rect rect = rects.get(i);
			Color drawColor = null;
			
			Point center = new Point(rect.x + rect.width * 0.5, rect.y
					+ rect.height * 0.5);
			
			if(i >= coloredRects.size()) {
				comboConfirmation++;
				
				if(comboConfirmation > 20) {
					Color newColor = getRandomColor();
					drawColor = newColor;
					coloredRects.add(new ColoredRect(center, newColor));
					comboConfirmation = 0;
				}
			} else {
				drawColor = getMinDistanceColor(center);
			}
			
			if(drawColor != null) {
				g.setColor(drawColor);
				g.fillOval((int)Math.round(center.x), (int)Math.round(center.y), 
						rect.width / 3, rect.height / 3);
			}
		}
	}
	
	public Color getRandomColor() {
		Random rand = new Random();
		
		float r = rand.nextFloat();
		float g = rand.nextFloat();
		float b = rand.nextFloat();
		
		return new Color(r, g, b);
	}
	
	//finds the color of the closest previous point registered
	public Color getMinDistanceColor(Point p) {
		double minDistance = Integer.MAX_VALUE;
		double distance;
		Point center;
		Color minDistanceColor = new Color(0,0,0);
		
		for(ColoredRect cRect : coloredRects) {
			center = cRect.center;
			distance = Math.sqrt((p.x-center.x)*(p.x-center.x) + 
					(p.y-center.y)*(p.y-center.y));
			if(distance < minDistance) {
				minDistance = distance;
				minDistanceColor = cRect.color;
			}
		}
		
		return minDistanceColor;
	}
	
	class ColoredRect {
		public Point center;
		public Color color;
		
		public ColoredRect(Point center, Color color) {
			this.center = center;
			this.color = color;
		}
	}
}

