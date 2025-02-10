package io.egorwhite.zaprett.ui.strategy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import io.egorwhite.zaprett.databinding.FragmentStrategyBinding;

public class StrategyFragment extends Fragment {

    private FragmentStrategyBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        StrategyViewModel StrategyViewModel =
                new ViewModelProvider(this).get(StrategyViewModel.class);

        binding = FragmentStrategyBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}