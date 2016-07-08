package com.fagi.model.messages.message;

import com.fagi.model.messages.Access;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by Sidheag on 2016-07-07.
 */
public interface VideoAcess extends Access<ArrayList<BufferedImage>> {
    @Override
    ArrayList<BufferedImage> getData();

    Message getMessage();
}
