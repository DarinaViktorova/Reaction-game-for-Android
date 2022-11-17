package com.example.reactiongame;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.example.reactiongame.databinding.FragmentFirstBinding;
import java.util.Map;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private Spinner gridSizeSpinner;
    private Spinner timerSpinner;
    private Spinner frameRateSpinner;

    private static final String ARG_PARAM1 = "grid";
    private static final String ARG_PARAM2 = "timer";
    private static final String ARG_PARAM3 = "range";

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gridSizeSpinner = view.findViewById(R.id.gridSizeSpinner);
        fillSpinner(gridSizeSpinner, Options.gridSizes);
        timerSpinner = view.findViewById(R.id.timerSpinner);
        fillSpinner(timerSpinner, Options.timers);
        frameRateSpinner = view.findViewById(R.id.frameRateSpinner);
        fillSpinner(frameRateSpinner, Options.frameRates);

        binding.goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(ARG_PARAM1, (String) gridSizeSpinner.getSelectedItem());
                bundle.putString(ARG_PARAM2, (String) timerSpinner.getSelectedItem());
                bundle.putString(ARG_PARAM3, (String) frameRateSpinner.getSelectedItem());

                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_from_FirstFragment_to_SecondFragment, bundle);
            }
        });
    }
    private void fillSpinner(Spinner spinner, Map map) {
        map.keySet();
        ArrayAdapter<String> adapter = new ArrayAdapter(spinner.getContext(), android.R.layout.simple_spinner_item, map.keySet().toArray(new String[0]));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}