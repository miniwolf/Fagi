package com.fagi.model.messages.message;

import com.fagi.model.messages.Access;
import com.fagi.model.messages.InGoingMessages;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by Sidheag on 2016-07-07.
 */
public class VideoMessage implements InGoingMessages, VideoAcess {
    transient ArrayList<BufferedImage> images;
    private Message message;

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(images.size()); // how many images are serialized?
        for (BufferedImage eachImage : images) {
            ImageIO.write(eachImage, "png", out); // png is lossless
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        final int imageCount = in.readInt();
        images = new ArrayList<BufferedImage>(imageCount);
        for (int i=0; i<imageCount; i++) {
            images.add(ImageIO.read(in));
        }
    }

    public VideoMessage(ArrayList<BufferedImage> buffImgs, String sender, long conversationID){
        images = buffImgs;
        message = new DefaultMessage(sender, conversationID);
    }

    @Override
    public Access getAccess() { return this; }

    @Override
    public ArrayList<BufferedImage> getData() {
        return images;
    }

    @Override
    public Message getMessage() {
        return null;
    }
}
