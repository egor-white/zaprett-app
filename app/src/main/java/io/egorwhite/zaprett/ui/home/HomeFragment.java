package io.egorwhite.zaprett.ui.home;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

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
        SwitchMaterial autostart = root.getRootView().findViewById(R.id.btn_toggle_autostart);
        Button startservice = root.getRootView().findViewById(R.id.btn_start_service);
        Button stopservice = root.getRootView().findViewById(R.id.btn_stop_service);
        ImageView statusbar = root.getRootView().findViewById(R.id.statusbarbg);
        ImageView statusicon = root.getRootView().findViewById(R.id.statusicon);
        TextView statustext = root.getRootView().findViewById(R.id.statustitle);

        fab.setOnClickListener(v -> {
            if (MainActivity.settings.getBoolean("use_module", false)) {
                ModuleInteractor.restartService();
                Snackbar.make(root.getRootView(), R.string.snack_reload, Snackbar.ANIMATION_MODE_FADE+Snackbar.LENGTH_SHORT).show();
            }
            else {
                Snackbar.make(root.getRootView(), R.string.snack_module_disabled, Snackbar.ANIMATION_MODE_FADE+Snackbar.LENGTH_SHORT).show();
            }
        });

        statusbar.setOnClickListener(v -> {
            if (MainActivity.settings.getBoolean("use_module", false)){
                if (ModuleInteractor.getStatus()){
                    statusicon.setImageResource(R.drawable.ic_enabled_black_24dp);
                    statustext.setText(R.string.status_enabled);
                }
                else {
                    statusicon.setImageResource(R.drawable.ic_disabled_black_24dp);
                    statustext.setText(R.string.status_disabled);
                }
            }
            else Snackbar.make(root.getRootView(), R.string.snack_module_disabled, Snackbar.ANIMATION_MODE_FADE+Snackbar.LENGTH_SHORT).show();
        });
        startservice.setOnClickListener(v -> {
            if (MainActivity.settings.getBoolean("use_module", false)){
                if (ModuleInteractor.getStatus()){
                    Snackbar.make(root.getRootView(), R.string.snack_already_started, Snackbar.ANIMATION_MODE_FADE+Snackbar.LENGTH_SHORT).show();
                }
                else {
                    ModuleInteractor.startService();
                    Snackbar.make(root.getRootView(), R.string.snack_starting_service, Snackbar.ANIMATION_MODE_FADE+Snackbar.LENGTH_SHORT).show();
                }
            }
            else Snackbar.make(root.getRootView(), R.string.snack_module_disabled, Snackbar.ANIMATION_MODE_FADE+Snackbar.LENGTH_SHORT).show();

        });
        stopservice.setOnClickListener(v -> {
            if (MainActivity.settings.getBoolean("use_module", false)){
                if (ModuleInteractor.getStatus()){
                    ModuleInteractor.stopService();
                    Snackbar.make(root.getRootView(), R.string.snack_stopping_service, Snackbar.ANIMATION_MODE_FADE+Snackbar.LENGTH_SHORT).show();
                }
                else {
                    Snackbar.make(root.getRootView(), R.string.snack_no_service, Snackbar.ANIMATION_MODE_FADE+Snackbar.LENGTH_SHORT).show();
                }
            }
            else Snackbar.make(root.getRootView(), R.string.snack_module_disabled, Snackbar.ANIMATION_MODE_FADE+Snackbar.LENGTH_SHORT).show();

        });
        autostart.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (MainActivity.settings.getBoolean("use_module", false)){
                ModuleInteractor.setStartOnBoot(isChecked);
            }
            else {
                buttonView.setChecked(false);
                Snackbar.make(root.getRootView(), R.string.snack_module_disabled, Snackbar.ANIMATION_MODE_FADE+Snackbar.LENGTH_SHORT).show();
            }
        });

        if (MainActivity.settings.getBoolean("use_module", false)&&MainActivity.settings.getBoolean("update_on_boot", false)) {
            if (ModuleInteractor.getStatus()){
                statusicon.setImageResource(R.drawable.ic_enabled_black_24dp);
                statustext.setText(R.string.status_enabled);
            }
            else {
                statusicon.setImageResource(R.drawable.ic_disabled_black_24dp);
                statustext.setText(R.string.status_disabled);
            }
            autostart.setChecked(ModuleInteractor.getStartOnBoot());
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}