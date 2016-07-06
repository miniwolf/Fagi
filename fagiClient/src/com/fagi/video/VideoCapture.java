package com.fagi.video;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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

    public void CaptureVideo(long frameCount) throws InterruptedException {
        if (frameCount <= 0){
            System.out.println("The hell you doin'?");
            return;
        }
        File file = new File("output.ts");

        IMediaWriter writer = ToolFactory.makeWriter(file.getName());
        Dimension size = WebcamResolution.QVGA.getSize();

        writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, size.width, size.height);

        Webcam webcam = Webcam.getDefault();
        webcam.setViewSize(size);
        webcam.open(true);

        long start = System.currentTimeMillis();

        for (int i = 0; i < frameCount; i++) {

            System.out.println("Capture frame " + i);

            BufferedImage image = ConverterFactory.convertToType(webcam.getImage(), BufferedImage.TYPE_3BYTE_BGR);
            IConverter converter = ConverterFactory.createConverter(image, IPixelFormat.Type.YUV420P);

            IVideoPicture frame = converter.toPicture(image, (System.currentTimeMillis() - start) * 1000);
            frame.setKeyFrame(i == 0);
            frame.setQuality(0);

            writer.encodeVideo(0, frame);

            // 60 FPS'ish
            Thread.sleep(17);
        }

        writer.close();

        System.out.println("Video recorded in file: " + file.getAbsolutePath());
    }
}