package com.fagi.video;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamMotionDetector;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

import javax.imageio.ImageIO;
import javax.swing.*;
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

    public void CaptureVideo() throws InterruptedException, IOException {
        Webcam webcam = Webcam.getDefault();
        if (webcam != null) {
            System.out.println("Webcam: " + webcam.getName());
        } else {
            System.out.println("No webcam detected");
            return;
        }

        System.out.println("Webcam FPS: " + webcam.getFPS());
        System.out.println("Viewsizes");
        Dimension[] viewSizes = webcam.getViewSizes();
        for (int i = 0; i < viewSizes.length; i++){
            System.out.println("Viewsize - Height: " + viewSizes[i].height + " - Width: " + viewSizes[i].width);
        }

        webcam.setViewSize(WebcamResolution.VGA.getSize());

        WebcamPanel panel = new WebcamPanel(webcam);
        panel.setFPSDisplayed(true);
        panel.setDisplayDebugInfo(true);
        panel.setImageSizeDisplayed(true);
        panel.setMirrored(true);

        JFrame window = new JFrame("Webcam JFrame");
        window.add(panel);
        window.setResizable(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack();
        window.setVisible(true);
        System.out.println("Video frame opened");
    }


}