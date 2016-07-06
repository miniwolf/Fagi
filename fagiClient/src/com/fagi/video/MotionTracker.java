package com.fagi.video;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamMotionDetector;
import com.github.sarxos.webcam.WebcamMotionEvent;
import com.github.sarxos.webcam.WebcamMotionListener;

/**
 * Created by Sidheag on 2016-07-06.
 */
public class MotionTracker implements WebcamMotionListener {

    public MotionTracker(int intervalInMs){
        WebcamMotionDetector detector = new WebcamMotionDetector(Webcam.getDefault());
        detector.setInterval(intervalInMs); // one check per 500 ms
        detector.addMotionListener(this);
        detector.start();
    }

    @Override
    public void motionDetected(WebcamMotionEvent webcamMotionEvent) {
        System.out.println("I SEE YOU MOVING BRUH");
    }
}
