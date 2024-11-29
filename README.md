# Android 动态加载 .dex文件

java 代码编译成 dex文件,并被Android 项目动态加载,然后执行 java方法

## 1 .java 源码编译成 dex 文件

https://github.com/dioq/Android_study/blob/main/bytecode/README.md

## 2 加载 .dex 文件并通过反射执行里面的方法

将生成的Target.dex 放在Android设备的目录下供 Android 应用加载,我这里放在 /data/local/tmp/
也可以放在别的地方只要Android应用可以读取得到。

### 2.1 加载 .dex 文件

String dex_path_str = "/data/local/tmp/";    </br>
String dex_filename = "Target.dex";    </br>
File dex_path = new File(dex_path_str,dex_filename); // dex 文件的外部存储路径    </br>
</br>
File dexOutputDir = this.getDir("dex", 0);// 无法直接从外部路径加载.dex文件，需要指定APP内部路径作为缓存目录（.dex文件会被解压到此目录）    </br>
</br>
// 加载dex 文件    </br>
//1.待加载的dex文件路径，如果是外存路径，一定要加上读外存文件的权限,    </br>
//2.解压后的dex存放位置，此位置仅该应用可读写    </br>
//3.指向包含本地库(so)的文件夹路径，可以设为null        </br>
//4.父级类加载器,一般可以通过context.getClassLoader() 获取到，也可以通过ClassLoader.getSystemClassLoader()取到。    </br>
DexClassLoader dexClassLoader = new DexClassLoader(dex_path.getAbsolutePath(), dexOutputDir.getAbsolutePath(), null, getClassLoader());

### 2.2 执行 .dex 文件里的方法

// 获取 java 类    </br>
Class clz = dexClassLoader.loadClass("com.my.target.TargetDexSourceFile");    </br>
// 获取类里的方法    </br>
Method dexRes = clz.getMethod("getMsgFromDexFile", String.class);    </br>
// 执行方法        </br>
String str = (String) dexRes.invoke(clz.newInstance(), "TestMsg");
