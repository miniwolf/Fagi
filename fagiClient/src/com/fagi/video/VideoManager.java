package com.fagi.video;

import com.fagi.model.Video;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
import com.github.sarxos.webcam.Webcam;
import main.FagiApp;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by Sidheag on 2016-07-07.
 */
public class VideoManager {
    private static Communication communication = null;
    private static Webcam activeCam;

    public static void close() {
        if (activeCam != null && activeCam.isOpen()){
            activeCam.close();
        }
    }

    public static void setCommunication(Communication communication) {
        VideoManager.communication = communication;
    }

    public static void setWebcam(Webcam webcam){
        VideoManager.activeCam = webcam;
    }

    public static void handleVideoTransmission(){
        // To prevent exceptions when requesting webcam
        Webcam webcam = VideoManager.activeCam;
        if (webcam.isOpen()){
            webcam.close();
        }
        int i = 0;
        do {
            if (webcam.getLock().isLocked()) {
                System.out.println("Waiting for lock to be released " + i);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    return;
                }
            } else {
                break;
            }
        } while (i++ < 3);

        String webcamName = webcam.getName();
        int j = 0;
        int secs = 0;
        try {
            ArrayList<BufferedImage> buffImgs = new ArrayList<>();
            while(webcam != null && webcam.isOpen() && VideoManager.activeCam != null && webcamName == VideoManager.activeCam.getName()){
                BufferedImage image = webcam.getImage();
                buffImgs.add(image);
                if (j % 30 == 0 && j > 0){
                    System.out.println("Seconds passed: " + secs);
                    secs++;
                    System.out.println("Passing images (" + buffImgs.size() + ")");
                    communication.sendObject(new Video(buffImgs));
                    buffImgs.clear();
                    System.out.println("Images passed");
                }
                Thread.sleep(33);
                j++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            VideoManager.close();
        }
    }
}
