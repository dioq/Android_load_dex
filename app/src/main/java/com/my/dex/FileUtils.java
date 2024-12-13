package com.my.dex;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

    private final String TAG = "dlog";

    private static FileUtils instance;

    //构造器私有化
    private FileUtils() {
    }

    //方法同步，调用效率低
    public static synchronized FileUtils getInstance() {
        if (instance == null) {
            instance = new FileUtils();
        }
        return instance;
    }

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

    // 复制文件
    public boolean copyFile(String srcPath, String dstPath) {
        try {
            File srcFile = new File(srcPath);
            File dstFile = new File(dstPath);
            // 判断源so文件是否存在
            if (!srcFile.exists()) {
                Log.d(TAG, srcPath + " not exists");
                return false;
            }
            if (dstFile.exists()) {
                Log.d(TAG, dstPath + " exists");
                if (dstFile.delete()) {
                    Log.d(TAG, dstPath + " delete success!");
                } else {
                    Log.d(TAG, dstPath + " delete fail!");
                    return false;
                }
            }

            FileInputStream fileInputStream = new FileInputStream(srcFile);
            FileOutputStream fileOutputStream = new FileOutputStream(dstFile);
            byte[] data = new byte[0x1000];
            int len = -1;
            while ((len = fileInputStream.read(data)) != -1) {
                fileOutputStream.write(data, 0, len);
                fileOutputStream.flush();
            }
            fileInputStream.close();
            fileOutputStream.close();
            return true;
        } catch (IOException e) {
            Log.d(TAG, "IOException:" + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
