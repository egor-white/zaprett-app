package io.egorwhite.zaprett.ui.home;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.net.Uri;
import android.content.Intent;
import android.content.DialogInterface;
import android.os.Build;
import java.io.File;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.RequiresApi;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import io.egorwhite.zaprett.MainActivity;
import io.egorwhite.zaprett.ModuleInteractor;
import io.egorwhite.zaprett.R;
import io.egorwhite.zaprett.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        FloatingActionButton fab = root.getRootView().findViewById(R.id.fab);
        SwitchMaterial autorestart = root.getRootView().findViewById(R.id.btn_toggle_autorestart);
        Button startservice = root.getRootView().findViewById(R.id.btn_start_service);
        Button stopservice = root.getRootView().findViewById(R.id.btn_stop_service);
        CardView statusbar = root.getRootView().findViewById(R.id.statuscard);
        ImageView statusicon = root.getRootView().findViewById(R.id.statusicon);
        TextView statustext = root.getRootView().findViewById(R.id.statustitle);
       
      if (new File(ModuleInteractor.getZaprettPath()).exists() && new File(ModuleInteractor.getZaprettPath()+"/config").exists()){
        if (requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean("use_module", false) && ModuleInteractor.getStartOnBoot()){
            autorestart.setChecked(true);
            Log.d("Enabled switch", "Enabled autorestart switch");
        }

        fab.setOnClickListener(v -> {
            if (requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean("use_module", false)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ModuleInteractor.restartService();
                    }
                }).start();
                Snackbar.make(root.getRootView(), R.string.snack_reload, Snackbar.ANIMATION_MODE_FADE+Snackbar.LENGTH_SHORT).show();
            }
            else {
                Snackbar.make(root.getRootView(), R.string.snack_module_disabled, Snackbar.ANIMATION_MODE_FADE+Snackbar.LENGTH_SHORT).show();
            }
        });

        statusbar.setOnClickListener(v -> {
            if (requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean("use_module", false)){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (ModuleInteractor.getStatus()){
                            statusicon.setImageResource(R.drawable.ic_enabled_black_24dp);
                            statustext.setText(R.string.status_enabled);
                        }
                        else {
                            statusicon.setImageResource(R.drawable.ic_disabled_black_24dp);
                            statustext.setText(R.string.status_disabled);
                        }
                    }
                }).start();
            }
            else Snackbar.make(root.getRootView(), R.string.snack_module_disabled, Snackbar.ANIMATION_MODE_FADE+Snackbar.LENGTH_SHORT).show();
        });
        startservice.setOnClickListener(v -> {
            if (requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean("use_module", false)){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (ModuleInteractor.getStatus()){
                            Snackbar.make(root.getRootView(), R.string.snack_already_started, Snackbar.ANIMATION_MODE_FADE+Snackbar.LENGTH_SHORT).show();
                        }
                        else {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    ModuleInteractor.startService();
                                }
                            }).start();
                            Snackbar.make(root.getRootView(), R.string.snack_starting_service, Snackbar.ANIMATION_MODE_FADE+Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }).start();
            }
            else Snackbar.make(root.getRootView(), R.string.snack_module_disabled, Snackbar.ANIMATION_MODE_FADE+Snackbar.LENGTH_SHORT).show();

        });
        stopservice.setOnClickListener(v -> {
            if (requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean("use_module", false)){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (ModuleInteractor.getStatus()){
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    ModuleInteractor.stopService();
                                }
                            }).start();
                            Snackbar.make(root.getRootView(), R.string.snack_stopping_service, Snackbar.ANIMATION_MODE_FADE+Snackbar.LENGTH_SHORT).show();
                        }
                        else {
                            Snackbar.make(root.getRootView(), R.string.snack_no_service, Snackbar.ANIMATION_MODE_FADE+Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }).start();

            }
            else Snackbar.make(root.getRootView(), R.string.snack_module_disabled, Snackbar.ANIMATION_MODE_FADE+Snackbar.LENGTH_SHORT).show();

        });
        autorestart.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean("use_module", false)){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ModuleInteractor.setStartOnBoot(isChecked);
                    }
                }).start();
                Snackbar.make(root.getRootView(), R.string.pls_reboot_snack, Snackbar.ANIMATION_MODE_FADE+Snackbar.LENGTH_SHORT).show();
            }
            else {
                buttonView.setChecked(false);
                Snackbar.make(root.getRootView(), R.string.snack_module_disabled, Snackbar.ANIMATION_MODE_FADE+Snackbar.LENGTH_SHORT).show();
            }
        });

        if (requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean("use_module", false)&&requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean("update_on_boot", false)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (ModuleInteractor.getStatus()){
                        statusicon.setImageResource(R.drawable.ic_enabled_black_24dp);
                        statustext.setText(R.string.status_enabled);
                    }
                    else {
                        statusicon.setImageResource(R.drawable.ic_disabled_black_24dp);
                        statustext.setText(R.string.status_disabled);
                    }
                }
            }).start();
        }
    } else { new MaterialAlertDialogBuilder(root.getContext())
                    .setTitle(R.string.error)
                    .setMessage(R.string.snack_no_lists)
                    .setPositiveButton(R.string.btn_continue, new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.R)
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("https://github.com/egor-white/zaprett"));
                            startActivity(intent);
                        }
                    })
                    .show();}

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}