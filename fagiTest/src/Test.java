import com.fagi.config.ServerConfig;
import com.fagi.encryption.AES;
import com.fagi.network.Communication;
import com.fagi.video.MotionTracker;
import com.fagi.video.VideoCapture;
import com.fagi.video.VideoManager;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by Sidheag on 2016-07-06.
 */
public class Test {
    public static void main(String [] args) {
//        VideoCapture capture = new VideoCapture();
//        try {
//            capture.GetWebcamPicture(false);
//            capture.CaptureVideo(); // Running ca. 60 FPS, meaning a 10 sec video
//        } catch (Exception ex){
//            System.out.println(ex.toString());
//        }
//
//        new MotionTracker(500); // Runs continously
//        try {
//            System.in.read();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
        try {
            String configFileLocation = "config/serverinfo.config";
            ServerConfig config = ServerConfig.pathToServerConfig(configFileLocation);
            AES aes = new AES();
            aes.generateKey(128);
            Communication communication = new Communication(config.getIp(), config.getPort(), aes, config.getServerKey());
            VideoManager.setCommunication(communication);
            VideoManager.setWebcam(Webcam.getDefault());
            VideoManager.handleVideoTransmission();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Capture finished");
    }
}
