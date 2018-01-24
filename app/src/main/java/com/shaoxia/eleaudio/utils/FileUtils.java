package com.shaoxia.eleaudio.utils;

import android.text.TextUtils;

import com.shaoxia.eleaudio.log.Logger;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by gonglt1 on 2018/1/24.
 */

public class FileUtils {
    private static final String TAG = "FileUtils";

    public static byte[] readFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        byte[] content = null;
        BufferedInputStream in;
        try {
       in  = new BufferedInputStream(new FileInputStream(path));
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);

            System.out.println("Available bytes:" + in.available());

            byte[] temp = new byte[1024];
            int size = 0;
            while ((size = in.read(temp)) != -1) {
                out.write(temp, 0, size);
            }
            in.close();

            content = out.toByteArray();
            Logger.d(TAG, "sendFile: Readed bytes count:" + content.length);
        } catch (IOException e) {
            e.printStackTrace();
            Logger.e(TAG, "sendFile: Readed bytes from file error");
        }
        return content;
    }
}
