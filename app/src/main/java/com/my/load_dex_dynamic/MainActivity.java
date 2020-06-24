package com.my.load_dex_dynamic;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void load_dex_func(View view) {
        loadDexClass();
    }

    /**
     * 加载dex文件中的class
     */
    private void loadDexClass() {
        //创建一个目录的，只有你自己的应用有权限访问
        File cacheFile = getDir("dex", 0);
        //File.separator 分割符
        String dex_store_path = cacheFile.getAbsolutePath() + File.separator + "target";
//        System.out.println("cacheFile.getAbsolutePath:\n" + cacheFile.getAbsolutePath());
//        System.out.println("dex_store_path:\n" + dex_store_path);
        File des_store_file = new File(dex_store_path);//classes2.dex 在本地保存的文件

        String dex_parsed_path = cacheFile.getAbsolutePath() + File.separator + "parsed_dex_file";
        File dex_parsed_folder = new File(dex_parsed_path);//target解析成dex 后存放的文件夹
        try {
            if (!des_store_file.exists()) {
                if (!des_store_file.createNewFile()) return;
                //把assets classes2.dex文件 里的二进制数据 复制到 本地文件里
                FileUtils.copyFiles(this, "classes2.dex", des_store_file);
            }
            if (!dex_parsed_folder.exists()) {
                if (!dex_parsed_folder.mkdir()) return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //下面开始加载dex class
        //1.待加载的dex文件路径，如果是外存路径，一定要加上读外存文件的权限,
        //2.解压后的dex存放位置，此位置一定要是可读写且仅该应用可读写
        //3.指向包含本地库(so)的文件夹路径，可以设为null
        //4.父级类加载器，一般可以通过Context.getClassLoader获取到，也可以通过ClassLoader.getSystemClassLoader()取到。
        DexClassLoader dexClassLoader = new DexClassLoader(dex_store_path, dex_parsed_path, null, getClassLoader());
        try {
            //该name就是dex_store_path路径下的dex文件里面的TestDexLoad这个类的包名+类名
            Class clz = dexClassLoader.loadClass("com.my.myshell_dex.test.TestDexLoad");
            Method dexRes = clz.getDeclaredMethod("getTestStr");
            String str = (String) dexRes.invoke(clz.newInstance());
            Toast.makeText(this, str, Toast.LENGTH_LONG)
                    .show();
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
