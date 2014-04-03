import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jzy3d.maths.Coord3d;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

/*
 * Authors: Alexander Chau, Cameron Ohrt
 * CIS 365 Artificial Intelligence
 * Project 3 - OpenCV
 * 
 * This class generates the frame components (webcam, graph, etc).
 */

public class WebcamFrame {
	public static void main(String[] args) {
		System.loadLibrary("opencv_java248");
		
		//initialize swing components
		JFrame frame = new JFrame("CIS 365 Detection");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		CVPanel dPanel = new CVPanel();
		HeatMapPanel hPanel = new HeatMapPanel(Settings.X_CELLS, Settings.Y_CELLS, 
				Settings.PANEL_WIDTH, Settings.PANEL_HEIGHT, Settings.HEAT_FACTOR);
		Coord3d[] defaultCoord = { new Coord3d(0,0,0) };
		GraphPanel gPanel = new GraphPanel(defaultCoord);
		
		JPanel contentPane = new JPanel(new GridLayout(2, 2));
		contentPane.add(dPanel);
		contentPane.add(hPanel);
		//contentPane.add(gPanel.initChart());
		
		frame.setContentPane(contentPane);
		frame.getContentPane().setBackground(Color.WHITE);
		
		//initialize face detector engine
		FaceDetector detector = new FaceDetector(
				"C:/opencv/sources/data/haarcascades/haarcascade_frontalface_alt.xml");
		
		//initialize webcam capture
		Mat webcamImage = new Mat();
		VideoCapture capture = new VideoCapture(0);
		
		if (capture.isOpened()) {
			contentPane.setSize(Settings.PANEL_WIDTH * 2 + 60,
					Settings.PANEL_HEIGHT * 2 + 60);
			frame.setSize(contentPane.getSize());
			
			while (true) {
				capture.read(webcamImage);
				if (!webcamImage.empty()) {
					Imgproc.resize(webcamImage, webcamImage, 
							new Size(Settings.PANEL_WIDTH, Settings.PANEL_HEIGHT));
					Core.flip(webcamImage, webcamImage, 1);
					webcamImage = detector.detectFaces(webcamImage);
					dPanel.matToBufferedImage(webcamImage);
					dPanel.repaint();
					
					Rect[] rects = detector.getRects();
					
					hPanel.setDetectedFaces(rects);
					hPanel.paintComponent(hPanel.getGraphics());
					
					gPanel.setPoints(gPanel.convertRectsToCoord3d(rects));
				} else {
					System.out.println("Can't find captured frame.");
					break;
				}
			}
		}
		return;
	}
}

//represents a class that can take each Mat captured to buffered images for analysis
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

//helper to generate Mat images to gray scale
class BinaryImageHelper extends CVPanel {
	private static final long serialVersionUID = 1L;
	
	public static boolean imageToBinaryScale(Mat matBGR) {
		Imgproc.GaussianBlur(matBGR, matBGR, new Size(3,3), 4);
		Imgproc.threshold(matBGR, matBGR, 30, 255, 
				Imgproc.THRESH_BINARY_INV);
		
		return true;
	}
}
