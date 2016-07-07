package com.fagi.video;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamMotionDetector;
import com.github.sarxos.webcam.WebcamMotionEvent;
import com.github.sarxos.webcam.WebcamMotionListener;

import java.io.IOException;

/**
 * Created by Sidheag on 2016-07-06.
 */
public class MotionTracker implements WebcamMotionListener {

    public MotionTracker(int intervalInMs){
        Webcam webcam = Webcam.getDefault();
        if (webcam != null) {
            System.out.println("Webcam: " + webcam.getName());
        } else {
            System.out.println("No webcam detected");
            return;
        }
        WebcamMotionDetector detector = new WebcamMotionDetector(webcam);
        detector.setInterval(intervalInMs); // one check per 500 ms
        detector.addMotionListener(this);
        detector.start();
    }

    @Override
    public void motionDetected(WebcamMotionEvent webcamMotionEvent) {
        System.out.println("INTRUDER ALERT! INTRUDER ALERT!");
        VideoCapture cap = new VideoCapture();
        try {
            cap.GetWebcamPicture(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
