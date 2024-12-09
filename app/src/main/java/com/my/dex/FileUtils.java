package com.my.dex;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileUtils {
    public byte[] readFile(String filePath) {
        String result = null;
        try {
            File f = new File(filePath);
            System.out.println(f.getAbsoluteFile());
            if (!f.exists()) {
                return null;
            }

            FileInputStream fin = new FileInputStream(f);
            int length = fin.available();
            byte[] buff = new byte[length];
            fin.read(buff);
            fin.close();
            return buff;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
