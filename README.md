# BaiduMusic
基于百度 music api(http)的音乐下载apk  




**********************************  
使用说明:  
1.项目使用 android studio 编译,所以需要下载编译的同学请准备 studio  
2.由于编译版本不同,在导入 studio的时候可能会遇到问题,需要修改以下3个地方  
  首先用本地的studio任意建议一个项目,对应本地项目,修改github的文件
  (1)BaiduMusic/gradle.properties 里面的 classpath 'com.android.tools.build:gradle:1.5.0'  
  (2)BaiduMusic/gradle/wrapper/gradle-wrapper.properties 里面的  
     distributionUrl=https\://services.gradle.org/distributions/gradle-2.8-all.zip  
  (3)BaiduMusic/app/build.gradle 里面的buildToolsVersion "23.0.2"
  
  完成以上3个修改,应该就可以正常导入了,如果还是不请,请自行对比github中的编译文件和 本地项目产生的编译文件有何不同之处    


