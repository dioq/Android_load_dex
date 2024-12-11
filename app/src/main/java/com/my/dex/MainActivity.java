package com.my.dex;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.my.androidloaddexfile.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;

import dalvik.system.DexClassLoader;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "dlog";
    private TextView showText;

    private Context context = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showText = findViewById(R.id.textBloardId);

        context = MainActivity.this;

        File filesDir_file = context.getFilesDir();
        String filesDir = "context.getFilesDir():" + filesDir_file.getPath();
        Log.d(TAG, filesDir);

        File cache_file = context.getCacheDir();
        String cacheDir = "context.getCacheDir():" + cache_file.getPath();
        Log.d(TAG, cacheDir);
    }

    public void load_dex(View view) {
        String dexName = "classes.dex";

        // dex 文件的外部存储路径
        String dexPath = "/data/local/tmp/" + dexName;

        File dexOutputDir = this.getDir("dex", 0);// 无法直接从外部路径加载.dex文件，需要指定APP内部路径作为缓存目录（.dex文件会被解压到此目录）
        Log.d(TAG, "dexOutputDir:" + dexOutputDir.toString());

        // 加载dex class
        //1.待加载的dex文件路径，如果是外存路径，一定要加上读外存文件的权限,
        //2.解压后的dex存放位置，此位置一定要是可读写且仅该应用可读写
        //3.指向包含本地库(so)的文件夹路径，可以设为null
        //4.父级类加载器，一般可以通过context.getClassLoader()获取到，也可以通过ClassLoader.getSystemClassLoader()取到。
        DexClassLoader dexClassLoader = new DexClassLoader(dexPath, dexOutputDir.getAbsolutePath(), null, getClassLoader());

        //调用 dex 文件里的 java方法
        try {
            //该name就是dex_store_path路径下的dex文件里面的TestDexLoad这个类的包名+类名
            Class<?> clz = dexClassLoader.loadClass("cn.my.study.Test");
            Method dexRes = clz.getMethod("getMsgFromDexFile", String.class);
            String str = (String) dexRes.invoke(clz.newInstance(), "TestMsg");
            Log.d(TAG, "msg from dex file:" + str);
            showText.setText(str);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                 InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void load_dex2(View view) {
        String dexName = "target.dex";
        // dex 文件的外部存储路径
        String dexPath = "/data/local/tmp/" + dexName;

        String className = "cn.my.study.Test";
        String methodName = "getMsgFromDexFile";

        Object ret = null;
        try {
            Class<?> cls1 = Class.forName("java.lang.String");
            Class<?>[] parameterTypes = new Class<?>[]{cls1};

            Object[] args = new Object[]{"AAAa"};

            ret = DexUtils.getInstance().loadDex(context, dexPath, className, methodName, parameterTypes, args);
        } catch (ClassNotFoundException | NoSuchMethodException |
                 IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        if (ret != null) {
            Log.d(TAG, "ret:" + ret);
            showText.setText(ret.toString());
        }
    }

    public void load_dex3(View view) {
        String current_process_name = "com.my.load";
        DexUtils.getInstance().inject(MainActivity.this, current_process_name);
    }
}