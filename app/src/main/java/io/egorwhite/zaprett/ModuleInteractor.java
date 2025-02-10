package io.egorwhite.zaprett;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
            if (startOnBoot) Runtime.getRuntime().exec(new String[]{"/system/bin/su","-c","touch /storage/emulated/0/zaprett/autostart"});
            else Runtime.getRuntime().exec(new String[]{"/system/bin/su","-c","rm -f /storage/emulated/0/zaprett/autostart"});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static boolean getStartOnBoot(){
        return new File("/storage/emulated/0/zaprett/autostart").exists();
    }
    public static String[] getAllLists() {
        try {
            java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(new String[]{"/system/bin/su","-c","/system/bin/zaprett getlists"}).getInputStream()).useDelimiter("\\A");
            return new String(s.hasNext() ? s.next() : "").split(" ");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String[]{};
    }
    public static String[] getActiveLists() {
        try {
            java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(new String[]{"/system/bin/su","-c","/system/bin/zaprett getactivelists"}).getInputStream()).useDelimiter("\\A");
            return new String(s.hasNext() ? s.next() : "").split(" ");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String[]{};
    }
    public static HashMap getLists() {
        HashMap<String, Boolean> lists = new HashMap<>();
        for (String list : getAllLists()) {
            lists.put(list, Arrays.stream(getActiveLists()).anyMatch(list::contains));
        }
        return lists;
    }
}
