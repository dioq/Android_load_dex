package com.my.dex;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
    private final String config_dir = "/data/local/tmp/work/";

    public void inject(Context context, String packageName) {
        Log.d(TAG, "inject dex ...");
        String dexPath = null;
        String className = null;
        String methodName = null;
        Class<?>[] parameterTypes = null;
        Object[] args = null;
        try {
            String config_path = config_dir + "config.json";
            byte[] bytes = new FileUtils().readFile(config_path);
            if (bytes == null) {
                Log.d(TAG, config_path + " not exist!");
                return;
            }
            String json_str = new String(bytes);
            JSONObject jsonRoot = new JSONObject(json_str);
            Log.d(TAG, jsonRoot.toString());

            JSONArray target_pkgname = jsonRoot.getJSONArray("target_pkgname");
//            for (int i = 0; i < target_pkgname.length(); i++) {
//                Log.d(TAG, target_pkgname.getString(i));
//            }

            boolean bool = target_pkgname.toString().contains(packageName);
            if (!bool) {
                return;
            }
            Log.d(TAG, packageName + " exist!");

            JSONObject dex = jsonRoot.getJSONObject("dex");
            dexPath = jsonRoot.getString("dir") + dex.getString("dexName");

            className = dex.getString("className");
            methodName = dex.getString("methodName");

            JSONArray param_JSONArray = dex.getJSONArray("parameterTypes");
            if (param_JSONArray.length() > 0) {
                parameterTypes = new Class[param_JSONArray.length()];
                for (int i = 0; i < param_JSONArray.length(); i++) {
                    parameterTypes[i] = Class.forName(param_JSONArray.getString(i));
                }
            }

            JSONArray args_JSONArray = dex.getJSONArray("args");
            if (args_JSONArray.length() > 0) {
                args = new Object[]{args_JSONArray.length()};
                for (int i = 0; i < args_JSONArray.length(); i++) {
                    args[i] = args_JSONArray.getString(i);
                }
            }

            Object ret = loadDex(context, dexPath, className, methodName, parameterTypes, args);
            Log.d(TAG, "ret:" + ret.toString());
        } catch (JSONException | ClassNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, "Exception:" + e.getMessage());
        }
    }

    public Object loadDex(Context context, String dexPath, String className, String methodName, Class<?>[] parameterTypes, Object[] args) {
        Log.d(TAG, "load dex ...");
        File dexOutputDir = context.getDir("dex", 0);
        DexClassLoader dexClassLoader = new DexClassLoader(dexPath, dexOutputDir.getAbsolutePath(), null, context.getClassLoader());

        //调用 dex 文件里的 java方法
        try {
            //该name就是dex_store_path路径下的dex文件里面的TestDexLoad这个类的包名+类名
            Class<?> clz = dexClassLoader.loadClass(className);
            Method dexRes = null;
            if (parameterTypes == null) {
                dexRes = clz.getMethod(methodName);
            } else {
                dexRes = clz.getMethod(methodName, parameterTypes);
            }
            Object obj = null;
            if (args == null) {
                obj = dexRes.invoke(clz.newInstance());
            } else {
                obj = dexRes.invoke(clz.newInstance(), args);
            }
            Log.d(TAG, "function from dex was invoke ...");
            return obj;
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                 InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            Log.d(TAG, "Exception:" + e.getMessage());
        }
        return null;
    }
}
