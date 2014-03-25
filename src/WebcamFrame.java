import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.JFrame;
import javax.swing.JPanel;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;

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
		frame.setSize(400, 400);
		frame.setVisible(true);
		DetectionPanel dPanel = new DetectionPanel();
		frame.setContentPane(dPanel);
		
		//initialize face detector engine
		FaceDetector detector = new FaceDetector(
				"C:/opencv/sources/data/haarcascades/haarcascade_frontalface_alt.xml");
		
		//initialize webcam capture
		Mat webcam_image = new Mat();
		VideoCapture capture = new VideoCapture(0);
		if (capture.isOpened()) {
			while (true) {
				capture.read(webcam_image);
				if (!webcam_image.empty()) {
					frame.setSize(webcam_image.width() + 40,
							webcam_image.height() + 60);
					// -- 3. Apply the classifier to the captured image
					webcam_image = detector.detect(webcam_image);
					// -- 4. Display the image
					dPanel.MatToBufferedImage(webcam_image); // We could look
																// at the
																// error...
					dPanel.repaint();
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
class DetectionPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private BufferedImage image;
	
	/**
	 * Converts/writes a Mat into a BufferedImage.
	 * 
	 * @param matrix
	 *            Mat of type CV_8UC3 or CV_8UC1
	 * @return BufferedImage of type TYPE_3BYTE_BGR or TYPE_BYTE_GRAY
	 */
	public boolean MatToBufferedImage(Mat matBGR) {
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