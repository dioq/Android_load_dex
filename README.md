# Android 动态加载 .dex文件
java 代码编译成 dex文件,并被Android 项目动态加载,然后执行 java方法

## 1. java源代码(.java)编译成java字节码(.class)
待编译的java类: TargetDexSourceFile.java		</br>
package com.my.target;					</br>
public class TargetDexSourceFile {			</br>
&emsp;public String getMsgFromDexFile(String param) {		</br>
&emsp;&emsp;return  param + "| come from dex file";		</br>
&emsp;}		</br>
}	</br>
直接使用 jdk 进行编译	</br>
javac TargetDexSourceFile.java </br>
生成	</br>
TargetDexSourceFile.class

## 2. java字节码(.class) 压缩成 Android ART 下的压缩包(.dex)
使用Android Sdk 里工具压缩 java 字节码	</br>
如我这里使用的工具	</br>
~/Library/Android/Sdk/build-tools/28.0.3/dx	</br>
在进行压缩前要先把 java 字节码的目录路径设置好,这个路径要 和 java 类的package 一致	</br>
新建如下目录(java_class 这个目录下存放所有需要压缩的java 字节码), 将待压缩的 java字节码放在里面	</br>
java_class/	</br>
&emsp;&emsp;└── com		</br>
&emsp;&emsp;&emsp;&emsp;&emsp;└── my		</br>
&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;└── target		</br>
&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;└── TargetDexSourceFile.class	</br>

执行压缩命令	</br>
dx --dex --output=Target.dex ./java_class/	</br>
生成 .dex文件	</br>
Target.dex

## 3. 加载 .dex 文件并通过反射执行里面的方法
将生成的Target.dex 放在Android设备的目录下供 Android 应用加载,我这里放在 /data/local/tmp/ 也可以放在别的地方只要Android应用可以读取得到。
### 3.1 加载 .dex 文件
String dex_path_str = "/data/local/tmp/";	</br>
String dex_filename = "Target.dex";	</br>
File dex_path = new File(dex_path_str,dex_filename); // dex 文件的外部存储路径	</br>
</br>
File dexOutputDir = this.getDir("dex", 0);// 无法直接从外部路径加载.dex文件，需要指定APP内部路径作为缓存目录（.dex文件会被解压到此目录）	</br>
</br>
// 加载dex 文件	</br>
//1.待加载的dex文件路径，如果是外存路径，一定要加上读外存文件的权限,	</br>
//2.解压后的dex存放位置，此位置仅该应用可读写	</br>
//3.指向包含本地库(so)的文件夹路径，可以设为null		</br>
//4.父级类加载器，一般可以通过context.getClassLoader()获取到，也可以通过ClassLoader.getSystemClassLoader()取到。	</br>
DexClassLoader dexClassLoader = new DexClassLoader(dex_path.getAbsolutePath(), dexOutputDir.getAbsolutePath(), null, getClassLoader());

### 3.2 执行 .dex 文件里的方法
// 获取 java 类	</br>
Class clz = dexClassLoader.loadClass("com.my.target.TargetDexSourceFile");	</br>
// 获取类里的方法	</br>
Method dexRes = clz.getMethod("getMsgFromDexFile", String.class);	</br>
// 执行方法		</br>
String str = (String) dexRes.invoke(clz.newInstance(), "TestMsg");
