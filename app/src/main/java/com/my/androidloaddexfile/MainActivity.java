package com.my.androidloaddexfile;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.my.target2.TargetDexSourceFile;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "loaddexfile";
    private TextView showText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showText = findViewById(R.id.textBloardId);
    }

    public void normal_test(View view) {
        TargetDexSourceFile tdf = new TargetDexSourceFile();
        String msg = tdf.getMsgFromDexFile("msg->app");
        showText.setText(msg);
    }

    public void load_dex(View view) {
        String dex_path_str = "/data/local/tmp/";
        String dex_filename = "Target.dex";
        File dex_path = new File(dex_path_str,dex_filename); // dex 文件的外部存储路径

        File dexOutputDir = this.getDir("dex", 0);// 无法直接从外部路径加载.dex文件，需要指定APP内部路径作为缓存目录（.dex文件会被解压到此目录）
        Log.d(TAG,"dexOutputDir:"+dexOutputDir.toString());

        // 加载dex class
        //1.待加载的dex文件路径，如果是外存路径，一定要加上读外存文件的权限,
        //2.解压后的dex存放位置，此位置一定要是可读写且仅该应用可读写
        //3.指向包含本地库(so)的文件夹路径，可以设为null
        //4.父级类加载器，一般可以通过context.getClassLoader()获取到，也可以通过ClassLoader.getSystemClassLoader()取到。
        DexClassLoader dexClassLoader = new DexClassLoader(dex_path.getAbsolutePath(), dexOutputDir.getAbsolutePath(), null, getClassLoader());

        //调用 dex 文件里的 java方法
        try {
            //该name就是dex_store_path路径下的dex文件里面的TestDexLoad这个类的包名+类名
            Class clz = dexClassLoader.loadClass("com.my.target.TargetDexSourceFile");
            Method dexRes = clz.getMethod("getMsgFromDexFile", String.class);
            String str = (String) dexRes.invoke(clz.newInstance(), "TestMsg");
            Log.d(TAG,"msg from dex file:" + str);
            showText.setText(str);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void load_dex2(View view) {

    }

}