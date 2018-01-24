package com.shaoxia.eleaudio.model;

/**
 * Created by gonglt1 on 2018/1/24.
 */

public class FileFrame {
    private byte[] data;
    private int index;

    public FileFrame(byte[] data, int index) {
        this.data = data;
        this.index = index;
    }


}
