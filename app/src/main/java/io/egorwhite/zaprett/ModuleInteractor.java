package io.egorwhite.zaprett;

import android.os.Environment;
import android.util.Log;

import com.google.firebase.BuildConfig;
import com.topjohnwu.superuser.Shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class ModuleInteractor {
    private static final String TAG = "ModuleInteractor";
    private static final String ZAPRETT_BIN = "/system/bin/zaprett";

    static {
        Shell.enableVerboseLogging = BuildConfig.DEBUG;
        Shell.setDefaultBuilder(Shell.Builder.create()
                .setFlags(Shell.FLAG_REDIRECT_STDERR)
                .setTimeout(10));
    }

    public static boolean checkRoot() {
        return Shell.getShell().isRoot();
    }

    public static boolean checkModuleInstallation() {
        Shell.Result result = Shell.cmd("[ -x " + ZAPRETT_BIN + " ] && " + ZAPRETT_BIN).exec();
        return result.isSuccess() && result.getOut().toString().contains("zaprett");
    }

    public static boolean getStatus() {
        Shell.Result result = Shell.cmd(ZAPRETT_BIN + " status").exec();
        return result.isSuccess() && result.getOut().toString().contains("working");
    }

    public static void restartService() {
        Shell.cmd(ZAPRETT_BIN + " restart").submit();
    }

    public static void startService() {
        Shell.cmd(ZAPRETT_BIN + " start").submit();
    }

    public static void stopService() {
        Shell.cmd(ZAPRETT_BIN + " stop").submit();
    }

    public static void setStartOnBoot(boolean startOnBoot) {
        Properties props = new Properties();
        File configFile = new File(getZaprettPath() + "/config");

        try (FileInputStream input = new FileInputStream(configFile)) {
            props.load(input);
            props.setProperty("autorestart", String.valueOf(startOnBoot));

            try (OutputStream output = new FileOutputStream(configFile)) {
                props.store(output, null);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error setting start on boot", e);
            throw new RuntimeException("Failed to update config", e);
        }
    }

    public static boolean getStartOnBoot() {
        Properties props = new Properties();
        try (FileInputStream input = new FileInputStream(getZaprettPath() + "/config")) {
            props.load(input);
            Log.d(TAG, "Use autorestart: " + props.getProperty("autorestart"));
            return Boolean.parseBoolean(props.getProperty("autorestart"));
        } catch (IOException e) {
            Log.e(TAG, "Error reading config", e);
            return false;
        }
    }

    public static String[] getAllLists() {
        File listsDir = new File(getZaprettPath() + "/lists/");
        String[] onlyNames = listsDir.list();
        if (onlyNames == null) return new String[0];

        String[] fullPath = new String[onlyNames.length];
        for (int i = 0; i < onlyNames.length; i++) {
            fullPath[i] = getZaprettPath() + "/lists/" + onlyNames[i];
        }
        return fullPath;
    }

    public static String[] getActiveLists() {
        Properties props = new Properties();
        try (FileInputStream input = new FileInputStream(getZaprettPath() + "/config")) {
            props.load(input);
            String activeLists = props.getProperty("activelists");
            Log.d(TAG, "Active lists: " + activeLists);
            return activeLists != null ? activeLists.split(",") : new String[0];
        } catch (IOException e) {
            Log.e(TAG, "Error reading active lists", e);
            return new String[0];
        }
    }

    public static void enableList(String path) {
        modifyActiveLists(true, path);
    }

    public static void disableList(String path) {
        modifyActiveLists(false, path);
    }

    private static void modifyActiveLists(boolean enable, String path) {
        Properties props = new Properties();
        File configFile = new File(getZaprettPath() + "/config");
        if (configFile.exists()){
            try (FileInputStream input = new FileInputStream(configFile)) {
                props.load(input);
                String currentLists = props.getProperty("activelists", "");

                List<String> lists = new ArrayList<>(Arrays.asList(currentLists.split(",")));
                lists = lists.stream()
                        .filter(s -> !s.trim().isEmpty())
                        .distinct()
                        .collect(Collectors.toList());

                if (enable) {
                    if (!lists.contains(path)) {
                        lists.add(path);
                    }
                } else {
                    lists.remove(path);
                }

                List<String> finalLists = lists.stream()
                        .distinct()
                        .collect(Collectors.toList());

                String newLists = String.join(",", finalLists);
                props.setProperty("activelists", newLists);

                try (OutputStream output = Files.newOutputStream(configFile.toPath())) {
                    props.store(output, "");
                }
            } catch (IOException e) {
                Log.e(TAG, "Error modifying active lists", e);
                throw new RuntimeException("Failed to update config", e);
            }
        }
        else {
            Log.e("Error modifying active lists", "No config file in zaprett directory");
        }
    }

    public static String getZaprettPath() {
        return "/storage/emulated/0/zaprett";
    }
}