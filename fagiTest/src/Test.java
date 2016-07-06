import com.fagi.video.VideoCapture;

/**
 * Created by Saros on 2016-07-06.
 */
public class Test {
    public static void main(String [] args) {
        VideoCapture capture = new VideoCapture();
        try {
            capture.GetWebcamPicPlix();
        } catch (Exception ex){
            System.out.println(ex.toString());
        }
    }
}
