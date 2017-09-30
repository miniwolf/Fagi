package com.fagi.uimodel;

import javafx.scene.image.Image;

public class FagiImage extends Image {
    private String url;

    public FagiImage(String url) {
        super(url);
        this.url = url;
    }

    public FagiImage(String url, int i, int i1, boolean b, boolean b1) {
        super(url, i, i1, b, b1);
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
