// HostsViewModel.java
package io.egorwhite.zaprett.ui.hosts;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.HashMap;
import java.util.Map;

public class HostsViewModel extends ViewModel {
    private static final MutableLiveData<Map<String, Boolean>> switchStates = new MutableLiveData<>(new HashMap<>());

    public static void setSwitchState(String listPath, boolean isChecked) {
        Map<String, Boolean> currentStates = switchStates.getValue();
        if (currentStates != null) {
            currentStates.put(listPath, isChecked);
            switchStates.setValue(currentStates);
        }
    }

    public Boolean getSwitchState(String listPath) {
        Map<String, Boolean> currentStates = switchStates.getValue();
        return currentStates != null ? currentStates.get(listPath) : null;
    }
}