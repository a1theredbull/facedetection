import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

/*
 * Authors: Alexander Chau, Cameron Ohrt
 * CIS 365 Artificial Intelligence
 * Project 3 - OpenCV
 * 
 * This class uses a Haar Cascade to detect faces.
 */


public class FaceDetector {
	private CascadeClassifier faceCascade;
	private Rect[] rects;
	
	public FaceDetector(String faceFilename) {
		//load cascade
		faceCascade = new CascadeClassifier(faceFilename);

		if (faceCascade.empty()) {
			System.err.println("Could not load cascades. Please check that xml files exist.");
			return;
		} else {
			System.out.println("Classifiers loaded.");
		}
	}
	
	public Rect[] getRects() {
		return rects;
	}
	
	// face detection
	public Mat detectFaces(Mat inputframe) {
		Mat mRgba = new Mat();
		Mat mGrey = new Mat();
		MatOfRect faces = new MatOfRect();
		inputframe.copyTo(mRgba);
		inputframe.copyTo(mGrey);
		//convert to grey scale
		Imgproc.cvtColor(mRgba, mGrey, Imgproc.COLOR_BGR2GRAY);
		Imgproc.equalizeHist(mGrey, mGrey);
		faceCascade.detectMultiScale(mGrey, faces);
		rects = faces.toArray();
		
		for (Rect rect : rects) {
			Point center = new Point(rect.x + rect.width * 0.5, rect.y
					+ rect.height * 0.5);
			Core.ellipse(mRgba, center, new Size(rect.width * 0.5,
					rect.height * 0.5), 0, 0, 360, new Scalar(255, 0, 255), 4,
					8, 0);
		}
		
		return mRgba;
	}
}
