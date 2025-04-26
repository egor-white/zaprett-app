package io.egorwhite.zaprett;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.topjohnwu.superuser.Shell;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import android.widget.Toast;
import androidx.navigation.ui.NavigationUI;

import java.io.File;
import java.io.IOException;

import io.egorwhite.zaprett.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    public static SharedPreferences settings;
    private FirebaseAnalytics mFirebaseAnalytics;
    private AppUpdater.UpdateCheckCallback updateCallback;


    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        settings = this.getSharedPreferences("settings", Context.MODE_PRIVATE);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_hosts, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        NavigationUI.setupWithNavController(binding.navView, navController);

        if (settings.getBoolean("welcome_dialog", true)) {
            new MaterialAlertDialogBuilder(MainActivity.this)
                    .setTitle(R.string.app_name)
                    .setMessage(R.string.text_welcome)
                    .setPositiveButton(R.string.btn_continue, null)
                    .show();
            settings.edit().putBoolean("welcome_dialog", false).apply();
        }
        if (!hasStorageManagementPermission(this)){
            new MaterialAlertDialogBuilder(MainActivity.this)
                    .setTitle(R.string.error_no_storage_title)
                    .setMessage(R.string.error_no_storage_message)
                    .setPositiveButton(R.string.btn_continue, new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.R)
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            requestStorageManagementPermission(MainActivity.this, 1337);
                        }
                    })
                    .show();
        }
        if(ModuleInteractor.checkRoot() && ModuleInteractor.checkModuleInstallation()) {
            settings.edit().putBoolean("use_module", true).apply();
            Log.d("Podsos module", "Module podsosan successefully");
        } else {
            Log.d("Error", "Podsos oshibka ebat");
            settings.edit().putBoolean("use_module", false).apply();
        }
    if(settings.getBoolean("autoupdate", true)){
        updateCallback = new AppUpdater.UpdateCheckCallback() {
            @Override
            public void onUpdateAvailable(AppUpdater.ReleaseInfo releaseInfo) {
                    new MaterialAlertDialogBuilder(MainActivity.this)
                        .setTitle(R.string.new_update)
                        .setMessage(String.format(getString(R.string.new_upd_text),releaseInfo.getVersion(),releaseInfo.getChangelog()))
                        .setPositiveButton(R.string.btn_download, (dialog, which) -> {
                            new AppUpdater(MainActivity.this, updateCallback).downloadUpdate(releaseInfo);
                        })
                        .setNegativeButton(R.string.btn_later, null)
                        .show();
                }
                @Override
                public void onNoUpdateAvailable(){
                    Log.i("Version", "Latest version installed");
                }
                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            };
        new AppUpdater(MainActivity.this, updateCallback).checkForUpdates();
    }
  }
    public static boolean hasStorageManagementPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Для Android 11+ используем официальный API
            return Environment.isExternalStorageManager();
        }
        else {
            // Для версий ниже Android 10 проверяем стандартное разрешение
            return ContextCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    /**
     * Запрашивает права на управление внешним хранилищем
     * Совместимо с Android 10 и выше
     */
    public static void requestStorageManagementPermission(Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Официальный способ для Android 11+
            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            activity.startActivityForResult(intent, requestCode);
        } else {
            // Для версий ниже Android 10 запрашиваем стандартное разрешение
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    requestCode);
        }
    }
}


