package io.egorwhite.zaprett.ui.hosts;

import static io.egorwhite.zaprett.R.drawable.*;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import io.egorwhite.zaprett.MainActivity;
import io.egorwhite.zaprett.ModuleInteractor;
import io.egorwhite.zaprett.R;
import io.egorwhite.zaprett.R.drawable;
import io.egorwhite.zaprett.databinding.FragmentHostsBinding;

public class HostsFragment extends Fragment {

    private FragmentHostsBinding binding;
    public LinearLayout listLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HostsViewModel HostsViewModel =
                new ViewModelProvider(this).get(HostsViewModel.class);

        binding = FragmentHostsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        listLayout = root.findViewById(R.id.listlayout);

        String[] allLists = ModuleInteractor.getAllLists();
        String[] activeLists = ModuleInteractor.getActiveLists();
        for(int i = 0; i < allLists.length; i++){
            String list = allLists[i];
            Log.d("Lists count", "Count: "+allLists.length);
            if (list!=null&&!list.isEmpty()){
                SwitchMaterial switchMaterial = new SwitchMaterial(root.getContext());
                CharSequence seq = list;
                switchMaterial.setText(seq);
                switchMaterial.setOnCheckedChangeListener((compoundButton, b) -> {
                    if (b) ModuleInteractor.enableList(list);
                    else ModuleInteractor.disableList(list);
                });
                listLayout.addView(switchMaterial);
                Log.i("Added element", "added switch for "+list);
            }
        }
        for(int i = 0; i < listLayout.getChildCount(); i++){
            SwitchMaterial switchMaterial = (SwitchMaterial) listLayout.getChildAt(i);
            switchMaterial.setChecked(Arrays.stream(activeLists).anyMatch(switchMaterial.getText().toString()::contains));
            for (int j = 0; j<activeLists.length; j++){
                if (switchMaterial.getText().toString().equals(activeLists[j])){
                    switchMaterial.setChecked(true);
                    break;
                }
            }
        }
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}