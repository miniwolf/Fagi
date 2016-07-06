import com.fagi.video.MotionTracker;
import com.fagi.video.VideoCapture;

import java.io.IOException;

/**
 * Created by Sidheag on 2016-07-06.
 */
public class Test {
    public static void main(String [] args) {
        VideoCapture capture = new VideoCapture();
        try {
            capture.GetWebcamPicture();
            capture.CaptureVideo(); // Running ca. 60 FPS, meaning a 10 sec video
        } catch (Exception ex){
            System.out.println(ex.toString());
        }

        new MotionTracker(500); // Runs continously
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
