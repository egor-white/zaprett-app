package io.egorwhite.zaprett.ui.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

import io.egorwhite.zaprett.MainActivity;
import io.egorwhite.zaprett.ModuleInteractor;
import io.egorwhite.zaprett.R;
import io.egorwhite.zaprett.databinding.FragmentSettingsBinding;
import io.egorwhite.zaprett.ui.home.HomeFragment;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    SwitchMaterial btn_use_module;
    SwitchMaterial btn_update_on_boot;
    SwitchMaterial btn_show_full_path;
    SwitchMaterial btn_autoupdate;

    @SuppressLint("CommitPrefEdits")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        //объявление объектов выключателей
        btn_use_module = root.findViewById(R.id.btn_use_module);
        btn_update_on_boot = root.findViewById(R.id.btn_update_on_create);
        btn_show_full_path = root.findViewById(R.id.btn_show_full_path);
        btn_autoupdate = root.findViewById(R.id.btn_autoupdate);
        //установка выключателей в значения из SharedPreferences
        btn_use_module.setChecked(requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean("use_module", false));
        btn_update_on_boot.setChecked(requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean("update_on_boot", false));
        btn_show_full_path.setChecked(requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean("show_full_path", true));
        btn_autoupdate.setChecked(requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean("autoupdate", true));

        btn_use_module.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (ModuleInteractor.checkRoot()){
                if (ModuleInteractor.checkModuleInstallation()){
                    if (isChecked){
                        requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE).edit().putBoolean("use_module", true).apply();
                        btn_update_on_boot.setEnabled(true);
                    }
                    else {
                        requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE).edit().putBoolean("use_module", false).apply();
                        requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE).edit().putBoolean("update_on_boot", false).apply();
                        btn_update_on_boot.setEnabled(false);
                        btn_update_on_boot.setChecked(false);
                    }
                }
                else {
                    new MaterialAlertDialogBuilder(root.getContext())
                            .setTitle(R.string.error_no_module_title)
                            .setMessage(R.string.error_no_module_message)
                            .setPositiveButton("OK", null)
                            .show();
                    buttonView.setChecked(false);
                }
            }
            else {
                new MaterialAlertDialogBuilder(root.getContext())
                        .setTitle(R.string.error_root_title)
                        .setMessage(R.string.error_root_message)
                        .setPositiveButton("OK", null)
                        .show();
                buttonView.setChecked(false);
                requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE).edit().putBoolean("use_module", false).apply();
            }
        });
        btn_update_on_boot.setOnCheckedChangeListener((buttonView, isChecked) -> {requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE).edit().putBoolean("update_on_boot", isChecked).apply();});
        btn_show_full_path.setOnCheckedChangeListener((buttonView, isChecked) -> {requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE).edit().putBoolean("show_full_path", isChecked).apply();});
        btn_autoupdate.setOnCheckedChangeListener((buttonView, isChecked) -> {requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE).edit().putBoolean("autoupdate", isChecked).apply();});


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
