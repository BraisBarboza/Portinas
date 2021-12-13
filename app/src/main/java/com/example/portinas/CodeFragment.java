package com.example.portinas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Random;

import butterknife.BindView;

public class CodeFragment extends Fragment {

    private int n = 10000 + new Random().nextInt(90000);
    String code,codebut;
    private TextView textViewcode;
    Button resetbut, linkbut;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_code, container, false);
        textViewcode = view.findViewById(R.id.code);
        resetbut = view.findViewById(R.id.resetcode_but);
        linkbut = view.findViewById(R.id.link_but);

        code = PreferencesConfig.loadCodefromPref(getActivity());
        if (code.equals(getString(R.string.defaultValue)))
        {
            PreferencesConfig.saveCodeinPref(getContext(),String.valueOf(n));
        }

        textViewcode.setText(code);

        resetbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int n2 = 10000 + new Random().nextInt(90000);
                PreferencesConfig.saveCodeinPref(getContext(),String.valueOf(n2));
                codebut = PreferencesConfig.loadCodefromPref(getActivity());
                textViewcode.setText(codebut);
            }
        });
        return view;
    }


}
