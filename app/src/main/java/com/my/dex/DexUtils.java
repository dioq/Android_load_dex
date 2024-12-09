package com.my.dex;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

import dalvik.system.DexClassLoader;

public class DexUtils {

    private static DexUtils instance;

    //构造器私有化
    private DexUtils() {
    }

    //方法同步，调用效率低
    public static synchronized DexUtils getInstance() {
        if (instance == null) {
            instance = new DexUtils();
        }
        return instance;
    }

    private final String TAG = "dlog";

    public Object loadDex(Context context, String dexPath, String className, String methodName, Class<?>[] parameterTypes, Object[] args) {
        Log.d(TAG, "load dex ...");
        File dexOutputDir = context.getDir("dex", 0);
        DexClassLoader dexClassLoader = new DexClassLoader(dexPath, dexOutputDir.getAbsolutePath(), null, context.getClassLoader());

        //调用 dex 文件里的 java方法
        try {
            //该name就是dex_store_path路径下的dex文件里面的TestDexLoad这个类的包名+类名
            Class<?> clz = dexClassLoader.loadClass(className);
            Method dexRes = clz.getMethod(methodName, parameterTypes);
            Object obj = dexRes.invoke(clz.newInstance(), args);
            Log.d(TAG, "function from dex was invoke ...");
            return obj;
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                 InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            Log.d(TAG, Objects.requireNonNull(e.getMessage()));
        }
        return null;
    }
}
