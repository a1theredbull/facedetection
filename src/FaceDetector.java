import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;


public class FaceDetector {
	private CascadeClassifier cascade;
	
	public FaceDetector(String cascadeFilename) {
		cascade = new CascadeClassifier(cascadeFilename);
		
		if (cascade.empty()) {
			System.err.println("Could not load cascade. Please check that xml file exists.");
			return;
		} else {
			System.out.println("Face classifier loaded.");
		}
	}
	
	public Mat detect(Mat inputframe) {
		Mat mRgba = new Mat();
		Mat mGrey = new Mat();
		MatOfRect faces = new MatOfRect();
		inputframe.copyTo(mRgba);
		inputframe.copyTo(mGrey);
		//convert to grey scale
		Imgproc.cvtColor(mRgba, mGrey, Imgproc.COLOR_BGR2GRAY);
		Imgproc.equalizeHist(mGrey, mGrey);
		cascade.detectMultiScale(mGrey, faces);
		System.out.println("Detected " + faces.toArray().length + " faces");
		for (Rect rect : faces.toArray()) {
			Point center = new Point(rect.x + rect.width * 0.5, rect.y
					+ rect.height * 0.5);
			Core.ellipse(mRgba, center, new Size(rect.width * 0.5,
					rect.height * 0.5), 0, 0, 360, new Scalar(255, 0, 255), 4,
					8, 0);
		}
		
		return mRgba;
	}
}
