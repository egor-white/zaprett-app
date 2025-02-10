package io.egorwhite.zaprett;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

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
        try {
            if (startOnBoot) Runtime.getRuntime().exec(new String[]{"/system/bin/su","-c","touch /data/zaprett/autostart"});
            else Runtime.getRuntime().exec(new String[]{"/system/bin/su","-c","rm -f /data/zaprett/autostart"});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static boolean getStartOnBoot(){
        return new File("/data/zaprett/autostart").exists();
    }
    public static File[] getAllLists(){
        return new File("/data/zaprett/lists").listFiles();
    }
}
