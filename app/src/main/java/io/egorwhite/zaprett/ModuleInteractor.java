package io.egorwhite.zaprett;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import android.os.Environment;

public class ModuleInteractor {
    public static boolean checkRoot() {
        try {
            java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(new String[]{"/system/bin/su","-c","cd / && ls"}).getInputStream()).useDelimiter("\\A");
            return !(s.hasNext() ? s.next() : "").isEmpty();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean checkModuleInstallation(){
        try {
            java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(new String[]{"/system/bin/su","-c","/system/bin/zaprett"}).getInputStream()).useDelimiter("\\A");
            return (s.hasNext() ? s.next() : "").contains("zaprett");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean getStatus(){
        try {
            java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(new String[]{"/system/bin/su","-c","/system/bin/zaprett status"}).getInputStream()).useDelimiter("\\A");
            return (s.hasNext() ? s.next() : "").contains("working");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static void restartService(){
        try {
            Runtime.getRuntime().exec(new String[]{"/system/bin/su","-c","/system/bin/zaprett restart"});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void startService(){
        try {
            Runtime.getRuntime().exec(new String[]{"/system/bin/su","-c","/system/bin/zaprett start"});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void stopService(){
        try {
            Runtime.getRuntime().exec(new String[]{"/system/bin/su","-c","/system/bin/zaprett stop"});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void setStartOnBoot(boolean startOnBoot){
        //TODO
    }
    public static boolean getStartOnBoot(){
        Properties props = new Properties();
        try {
            FileInputStream input = new FileInputStream(Environment.getExternalStorageDirectory()+"/zaprett/config");
            props.load(input);
            Log.d("Autostart",  "Use autostart: "+props.getProperty("autostart"));
            return props.getProperty("autostart").contains("true");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static String[] getAllLists() {
        return new File(String.valueOf(Environment.getExternalStorageDirectory())+"/zaprett/lists/").list();
    }
    public static String[] getActiveLists() {
        Properties props = new Properties();
        try {
            FileInputStream input = new FileInputStream(Environment.getExternalStorageDirectory()+"/zaprett/config");
            props.load(input);
            Log.d("Active lists",  props.getProperty("activelists"));
            return props.getProperty("activelists").split(",");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void enableList(String path){
        Properties props = new Properties();
        try {
            FileInputStream input = new FileInputStream(Environment.getExternalStorageDirectory()+"/zaprett/config");
            OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory()+"/zaprett/config");
            props.load(input);
            if (props.getProperty("activelists")==null) props.setProperty("activelists", path);
            else props.setProperty("activelists", props.getProperty("activelists")+", "+path);
            props.setProperty("autostart", props.getProperty("autostart"));
            props.setProperty("zaprettdir", props.getProperty("zaprettdir"));
            props.store(output, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void disableList(String path){
        //TODO
    }
    public static String getZaprettPath(){
        Properties props = new Properties();
        try {
            FileInputStream input = new FileInputStream(Environment.getExternalStorageDirectory()+"/zaprett/config");
            props.load(input);
            return props.getProperty("zaprettdir");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
