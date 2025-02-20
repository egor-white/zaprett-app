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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;

import android.os.Environment;

public class ModuleInteractor {
    public static boolean checkRoot() {
        try {
            Scanner s = new Scanner(Runtime.getRuntime().exec(new String[]{"/system/bin/su","-c","cd / && ls"}).getInputStream()).useDelimiter("\\A");
            return !(s.hasNext() ? s.next() : "").isEmpty();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean checkModuleInstallation(){
        try {
            Scanner s = new Scanner(Runtime.getRuntime().exec(new String[]{"/system/bin/su","-c","/system/bin/zaprett"}).getInputStream()).useDelimiter("\\A");
            return (s.hasNext() ? s.next() : "").contains("zaprett");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean getStatus(){
        try {
            Scanner s = new Scanner(Runtime.getRuntime().exec(new String[]{"/system/bin/su","-c","/system/bin/zaprett status"}).getInputStream()).useDelimiter("\\A");
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
        Properties props = new Properties();
        try {
            FileInputStream input = new FileInputStream(Environment.getExternalStorageDirectory()+"/zaprett/config");
            props.load(input);
            props.setProperty("autostart", String.valueOf(startOnBoot));
            OutputStream output = new FileOutputStream(getZaprettPath()+"/config");
            props.store(output, "Don't place '/' in end of directory! Example: /sdcard");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static boolean getStartOnBoot(){
        Properties props = new Properties();
        try {
            FileInputStream input = new FileInputStream(Environment.getExternalStorageDirectory()+"/zaprett/config");
            props.load(input);
            Log.d("Autostart",  "Use autostart: "+props.getProperty("autostart"));
            return props.getProperty("autostart").contains("true")||props.getProperty("autostart").contains("1");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static String[] getAllLists() {
        String[] onlyNames = new File(getZaprettPath()+"/lists/").list();
        String[] fullPath = new String[Objects.requireNonNull(onlyNames).length];
        for(int i = 0; i < onlyNames.length; i++){
            fullPath[i] = getZaprettPath()+"/lists/"+onlyNames[i];
        }
        return fullPath;
    }
    public static String[] getActiveLists() {
        Properties props = new Properties();
        try {
            FileInputStream input = new FileInputStream(getZaprettPath()+"/config");
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
            props.load(input);
            if (!props.getProperty("activelists").contains(path)){
                if (props.getProperty("activelists")!=null) props.setProperty("activelists", props.getProperty("activelists")+","+path);
                else props.setProperty("activelists", path);
            }
            OutputStream output = new FileOutputStream(getZaprettPath()+"/config");
            props.store(output, "Don't place '/' in end of directory! Example: /sdcard");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void disableList(String path){
        Properties props = new Properties();
        try {
            FileInputStream input = new FileInputStream(Environment.getExternalStorageDirectory()+"/zaprett/config");
            props.load(input);
            if (props.getProperty("activelists").contains(path)){
                ArrayList<String> lists = new ArrayList<>(Arrays.asList(props.getProperty("activelists").split(",")));
                lists.remove(path);
                String actlists = "";
                for (int i = 0; i < lists.size(); i++){
                    if (i<(lists.size()-1)) actlists+=(lists.get(i)+",");
                    else actlists+=lists.get(i);
                }
                props.setProperty("activelists", actlists);
            }
            OutputStream output = new FileOutputStream(getZaprettPath()+"/config");
            props.store(output, "Don't place '/' in end of directory! Example: /sdcard");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
