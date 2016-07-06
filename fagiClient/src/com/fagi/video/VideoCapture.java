package com.fagi.video;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by Sidheag on 2016-07-06.
 */
public class VideoCapture
{
    public void GetWebcamPicture() throws IOException
    {
        Webcam webcam = Webcam.getDefault();
        if (webcam != null) {
            System.out.println("Webcam: " + webcam.getName());
        } else {
            System.out.println("No webcam detected");
        }
        if (webcam != null){
            if (!webcam.isOpen()){
               webcam.open();
            }
            // get image
            BufferedImage image = webcam.getImage();

            ByteBuffer imageBytes = webcam.getImageBytes();

            // save image to PNG file
            ImageIO.write(image, "PNG", new File("webcam-pic-plix.png"));

            webcam.close();
        }
    }

    public void CaptureVideo(long frameCount) throws InterruptedException, IOException {
        if (frameCount <= 0){
            System.out.println("The hell you doin'?");
            return;
        }
        Webcam webcam = Webcam.getDefault();
        if (webcam != null) {
            System.out.println("Webcam: " + webcam.getName());
        } else {
            System.out.println("No webcam detected");
        }
        ByteBuffer imageBytes = webcam.getImageBytes();
        FileOutputStream fos = new FileOutputStream("c:/dat-video-dawg.mov");
        fos.write(imageBytes.array());
        fos.close();
        webcam.close();
        System.out.println("Video recorded in file");
    }
}