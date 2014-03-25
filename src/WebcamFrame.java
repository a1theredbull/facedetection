import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.JFrame;
import javax.swing.JPanel;
import org.opencv.core.Mat;
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
		JPanel contentPane = new JPanel(new GridLayout(2, 0));
		contentPane.add(dPanel);
		contentPane.add(bPanel);
		
		frame.setContentPane(contentPane);
		
		//initialize face detector engine
		FaceDetector detector = new FaceDetector(
				"C:/opencv/sources/data/haarcascades/haarcascade_frontalface_alt.xml");
		
		//initialize webcam capture
		Mat webcamImage = new Mat();
		VideoCapture capture = new VideoCapture(0);
		if (capture.isOpened()) {
			while (true) {
				capture.read(webcamImage);
				if (!webcamImage.empty()) {
					Imgproc.resize(webcamImage, webcamImage, new Size(400, 300));
					frame.setSize(webcamImage.width() + 40,
							webcamImage.height() * 2 + 80);
					contentPane.setSize(webcamImage.width() + 40,
							webcamImage.height() * 2 + 60);
					webcamImage = detector.detectFaces(webcamImage);
					dPanel.matToBufferedImage(webcamImage);
					dPanel.repaint();
					
					bPanel.imageToBinaryScale(webcamImage);
					bPanel.matToBufferedImage(webcamImage);
					bPanel.repaint();
				} else {
					System.out.println(" --(!) No captured frame -- Break!");
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
		Imgproc.threshold(matBGR, matBGR, 160, 200, 
				Imgproc.THRESH_BINARY_INV);
		
		return true;
	}
}