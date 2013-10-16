package com.rock.alarmclock.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class CompiledApkUpdate {

    private static final String androidSDK_PATH = "C:\\Users\\a\\Desktop\\adt-bundle-windows-x86_64-20130717\\sdk";        //android SDK路径

    public static final String APK_NAME = "疯狂的闹钟.apk";
    public static final String PROJECT_LIBARY = "";
    public static final String PROJECT_PATH = "C:\\Users\\a\\Desktop\\Alarm Clock\\Alarm Clock";        //要打包的工程路径
    public static final String APK_PATH = "C:\\Users\\a\\Desktop\\疯狂的闹钟_";        //打包后存放apk的路径  duitang_是前缀
    
    
    private static final String apk_PATH_keystore = "C:\\Users\\a\\Desktop\\keystore\\";        //apk签名文件路径
    private static final String channelFlag = "channel_name";
    
//    public static String[] channels = {"duitang"}; 
    private static String currentChannelName = "";
    public static String[] channels = {"duitang","91","360","QQ","jifeng","anzhuo","anzhi","youyi","appchina","wangyi","baidu","souhu","3g","nduo","xiaomi","huawei","meizu","lianxiang","aliyun","taobao","google","nearme","mumayi","wandoujia","crosscat","dangle","maopao","feiliu"}; 

    public static void main(String[] args) { 
        replaceChannel();
    }

    /**
     * 替换渠道名称
     */
    public static void replaceChannel() {
        try {
            String outPath = PROJECT_PATH + "res\\values\\strings.xml"; // 输出文件位置
            String content = read(outPath);
            for(int channelid=0;channelid<channels.length;channelid++){
                String tmpContent = content;
                tmpContent = tmpContent.replaceFirst(channelFlag, channels[channelid]);
                currentChannelName = channels[channelid];
                write(tmpContent,outPath);
                System.out.println("replace channel name over...");
                packageRes(); // 一次渠道号的更改完成。可以进行打包了。
                createUnsignedApk();
                signedApk(channelid);
            }
            write(content,outPath);        //完成后还原channel_name
            System.out.println("execute over!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * class文件打包成classes.dex
     */
    public static void packageDex(){
        try { 
            System.out.println("dx.bat start...");
            Process process = Runtime.getRuntime().exec(androidSDK_PATH
                    +"platform-tools\\dx.bat --dex --output="
                    +PROJECT_PATH+"bin\\classes.dex "
                    +PROJECT_PATH+"bin\\classes "
                    +PROJECT_PATH+"libs\\*.jar"); 
            
            new MyThread(process.getErrorStream()).start();

            new MyThread(process.getInputStream()).start();
            
            process.waitFor();  
            process.destroy();  
            System.out.println("dx.bat over...");
        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
    }
    
    /**
     * res assets文件打包成res.zip
     */
    public static void packageRes(){
        try{
            System.out.println(currentChannelName+" create resources.ap");
            String library = "";
            if(PROJECT_LIBARY!=null&&!PROJECT_LIBARY.equals("")){
                library = "-S " + PROJECT_LIBARY + "res ";
            }
            Process process = null;
            process = Runtime
                    .getRuntime()
                    .exec(  androidSDK_PATH
                            + "platform-tools\\aapt.exe package -f " +
                            "-M " + PROJECT_PATH + "AndroidManifest.xml " +            //-M 指定配置文件
                            "-S " + PROJECT_PATH + "res " +                            //-S 指定资源文件
                            library +
                            "-A " + PROJECT_PATH + "assets " +                        //-A 指定assets
                            "-I " + androidSDK_PATH + "platforms\\android-16\\android.jar " +    //-I 指定android类
                            "-F " + PROJECT_PATH + "bin\\resources.ap_ --auto-add-overlay"); // 将资源文件打包resources.ap_
            new MyThread(process.getErrorStream()).start();
            new MyThread(process.getInputStream()).start();
            process.waitFor();
            process.destroy();
            System.out.println(currentChannelName+" resources.ap over...");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * classes.dex res.zip AndroidManifest.xml组合成未签名的apk
     */
    public static void createUnsignedApk(){
        try{
            System.out.println(currentChannelName+" createUnsignedApk start");
            Process process = null;
            process = Runtime.getRuntime().exec(
                    androidSDK_PATH+ "tools\\apkbuilder.bat "
                    + PROJECT_PATH + "bin\\"+APK_NAME+" -u -z "
                    + PROJECT_PATH + "bin\\resources.ap_ -f "
                    + PROJECT_PATH + "bin\\classes.dex"); // 生成未签名的apk
            new MyThread(process.getErrorStream()).start();
            new MyThread(process.getErrorStream()).start();
            process.waitFor();
            process.destroy();
            System.out.println(currentChannelName+" createUnsignedApk over");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * 签名apk
     */
    public static void signedApk(int channelid){
        try{
            System.out.println(currentChannelName+" signed apk start");
            Process process = null;
            String jarsigner;
            jarsigner = "jarsigner -keystore "+apk_PATH_keystore+" -storepass ***** -keypass ****** " +
                    "-signedjar "
                    + APK_PATH
                    + channels[channelid]
                    + ".apk "
                    + PROJECT_PATH
                    + "bin\\"+APK_NAME+" *****";            //签名apk
            process = Runtime
                    .getRuntime()
                    .exec(jarsigner); // 对apk进行签名
            new MyThread(process.getErrorStream()).start();

            new MyThread(process.getInputStream()).start();
            process.waitFor();
            process.destroy();
            System.out.println(currentChannelName+" signed apk over"); // 一条渠道的打包完成。文件会输出到指定目录
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    public static String read(String path) {
        StringBuffer res = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path),"UTF-8"));
            while ((line = reader.readLine()) != null) {
                res.append(line + "\n");
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res.toString();
    }

    public static boolean write(String cont, String dist) {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(new File(dist)),"utf-8");
            writer.write(cont);
            writer.flush();
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    public static class MyThread extends Thread{
        BufferedReader bf;
        
        public MyThread(InputStream input) {
            bf = new BufferedReader(new InputStreamReader(input));
        }

        public void run() {
            String line;
            try {
                line = bf.readLine();
                while (line != null) {
                    System.out.println(line);
                    line = bf.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}