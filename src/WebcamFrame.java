import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.rendering.canvas.CanvasNewtAwt;
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

public class WebcamFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final String LIB_BIN = "/lib-bin/";
	
	private JPanel contentPane;
	private CVPanel dPanel;
	private HeatMapPanel hPanel;
	private GraphPanel gPanel;
	private JPanel bPanel;
	
	private Rect[] rects;
	
	static {
		try {
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		} catch(Exception e) {
			System.err.println("no opencv dll found");
		}
	}
	
	public static void main(String[] args) {
		//initialize swing components
		WebcamFrame frame = new WebcamFrame("CIS 365 Detection");
		return;
	}
	
	public WebcamFrame(String title) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		dPanel = new CVPanel();
		hPanel = new HeatMapPanel(Settings.X_CELLS, Settings.Y_CELLS, 
				Settings.PANEL_WIDTH, Settings.PANEL_HEIGHT, Settings.HEAT_FACTOR);
		bPanel = createButtonPanel();
		gPanel = new GraphPanel();
		
		contentPane = new JPanel(new GridLayout(2, 2));
		contentPane.add(dPanel);
		contentPane.add(hPanel);
		contentPane.add(bPanel);
		contentPane.add((CanvasNewtAwt)gPanel.initChart().getCanvas());
		
		setContentPane(contentPane);
		getContentPane().setBackground(Color.WHITE);
		
		String decoded = "";
		try {
			decoded = URLDecoder.decode(WebcamFrame.class.getResource(
					"resources/haarcascade_frontalface_alt.xml").getPath(), "UTF-8");
			System.out.println(decoded);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		//initialize face detector engine
		FaceDetector detector = new FaceDetector(decoded.substring(1));
		
		//initialize webcam capture
		Mat webcamImage = new Mat();
		VideoCapture capture = new VideoCapture(0);
		
		if (capture.isOpened()) {
			contentPane.setSize(Settings.PANEL_WIDTH * 2 + 60,
					Settings.PANEL_HEIGHT * 2 + 60);
			setSize(contentPane.getSize());
			
			while (true) {
				capture.read(webcamImage);
				if (!webcamImage.empty()) {
					Imgproc.resize(webcamImage, webcamImage, 
							new Size(Settings.PANEL_WIDTH, Settings.PANEL_HEIGHT));
					Core.flip(webcamImage, webcamImage, 1);
					webcamImage = detector.detectFaces(webcamImage);
					dPanel.matToBufferedImage(webcamImage);
					dPanel.repaint();
					
					rects = detector.getRects();
					
					hPanel.setDetectedFaces(rects);
					hPanel.paintComponent(hPanel.getGraphics());
					
					Coord3d[] points = gPanel.convertRectsToCoord3d(rects);
					gPanel.addPoints(points);
				} else {
					System.out.println("Can't find captured frame.");
					break;
				}
			}
		}
	}
	
	private JPanel createButtonPanel() {
		JPanel bPanel = new JPanel();
		bPanel.setLayout(new GridLayout(2,2));
		JButton resetHeatMapButton = new JButton("RESET HEAT MAP");
		bPanel.add(resetHeatMapButton);
		resetHeatMapButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				hPanel.resetGrid();
			}
		});
		
		JButton plotGraph = new JButton("PLOT GRAPH");
		bPanel.add(plotGraph);
		plotGraph.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				contentPane.remove(3);
				CanvasNewtAwt graphCanvas = (CanvasNewtAwt)
						gPanel.initChart().getCanvas();
				contentPane.add(graphCanvas);
				setVisible(true); //graph only shows with this...(wtf swing)
			}
		});
		
		return bPanel;
	}
}

/* Class that can convert each Mat captured to buffered 
 * images for analysis
 */
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
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (this.image == null)
			return;
		g.drawImage(this.image, 10, 10, this.image.getWidth(),
				this.image.getHeight(), null);
	}
}