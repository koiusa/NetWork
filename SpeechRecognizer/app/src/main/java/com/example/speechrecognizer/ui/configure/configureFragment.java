package com.example.speechrecognizer.ui.configure;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.speechrecognizer.App;
import com.example.speechrecognizer.MainActivity;
import com.example.speechrecognizer.R;
import com.example.speechrecognizer.commnon.components.Configure;
import com.example.speechrecognizer.commnon.network.Dispatcher;

public class configureFragment extends Fragment {
    private Configure configure = null;
    private ConfigureViewModel configureViewModel;
    private Dispatcher tClient = null;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        configureViewModel =
                new ViewModelProvider(this).get(ConfigureViewModel.class);
        View root = inflater.inflate(R.layout.fragment_configure, container, false);
        final TextView textView = root.findViewById(R.id.text_configure);
        configureViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        configure = new Configure(getContext());
        MainActivity main = (MainActivity)getActivity();
        App app = (App) main.getApplication();
        tClient = app.getDispatcher();
        TextView ipAddress = root.findViewById(R.id.configure_ip);
        ipAddress.setText(configure.getIPAddress());
        ipAddress.setEnabled(configure.getUseSend());
        ipAddress.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                configure.setIPAddress(s.toString());
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        TextView port = root.findViewById(R.id.configure_port);
        port.setText(String.valueOf(configure.getPort()));
        port.setEnabled(configure.getUseSend());
        port.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                configure.setPort(Integer.valueOf(s.toString()));
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        SwitchCompat useSend = root.findViewById(R.id.configure_send_switch);
        useSend.setChecked(configure.getUseSend());
        useSend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                configure.setUseSend(isChecked);
                ipAddress.setEnabled(isChecked);
                port.setEnabled(isChecked);
                if (isChecked){
                    tClient.startSequence(configure.getIPAddress(),configure.getPort());
                }else{
                    tClient.disConnect();
                }
            }
        });

        TextView suffix = root.findViewById(R.id.configure_suffix);
        suffix.setText(String.valueOf(configure.getSuffix()));
        suffix.setEnabled(configure.getUseSuffix());
        suffix.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                configure.setSuffix(s.toString());
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        SwitchCompat useSuffix = root.findViewById(R.id.configure_suffix_switch);
        useSuffix.setChecked(configure.getUseSuffix());
        useSuffix.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                configure.setUseSuffix(isChecked);
                suffix.setEnabled(isChecked);
            }
        });

        Spinner spinner = (Spinner)root.findViewById(R.id.configure_language);
        String[] labels = getResources().getStringArray(R.array.language_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(root.getContext(), android.R.layout.simple_spinner_item, labels);
        spinner.setAdapter(adapter);
        spinner.setEnabled(configure.getUseLanguage());
        SwitchCompat useLanguage = root.findViewById(R.id.configure_language_switch);
        useLanguage.setChecked(configure.getUseLanguage());
        useLanguage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                configure.setUseLanguage(isChecked);
                spinner.setEnabled(isChecked);
            }
        });

        SwitchCompat onLine = root.findViewById(R.id.configure_prefer_offline_switch);
        onLine.setChecked(configure.getOnline());
        onLine.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                configure.setOnline(isChecked);
            }
        });
        return root;
    }
}