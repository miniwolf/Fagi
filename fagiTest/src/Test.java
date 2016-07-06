import com.fagi.video.VideoCapture;

/**
 * Created by Sidheag on 2016-07-06.
 */
public class Test {
    public static void main(String [] args) {
        VideoCapture capture = new VideoCapture();
        try {
            capture.GetWebcamPicture();
            capture.CaptureVideo(600); // Running ca. 60 FPS, meaning a 10 sec video
        } catch (Exception ex){
            System.out.println(ex.toString());
        }
    }
}
