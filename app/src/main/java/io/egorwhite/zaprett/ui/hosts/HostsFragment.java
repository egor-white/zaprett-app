package io.egorwhite.zaprett.ui.hosts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import io.egorwhite.zaprett.databinding.FragmentHostsBinding;

public class HostsFragment extends Fragment {

    private FragmentHostsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HostsViewModel HostsViewModel =
                new ViewModelProvider(this).get(HostsViewModel.class);

        binding = FragmentHostsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}