package com.example.portinas;

import static com.example.portinas.MainActivity.mDatabase;

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

public class CodeFragment extends Fragment {

    private int n = 10000 + new Random().nextInt(90000);
    public static String codebutoff, codebuton = "";
    private TextView textViewcode;
    Button resetbut, linkbut;
    EditText linket;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_code, container, false);
        textViewcode = view.findViewById(R.id.code);
        resetbut = view.findViewById(R.id.resetcode_but);
        linkbut = view.findViewById(R.id.link_but);
        linket = view.findViewById(R.id.link_et);

        codebutoff = PreferencesConfig.loadCodefromPref(getActivity());
        if (codebutoff.equals(getString(R.string.defaultValue)))
        {
            PreferencesConfig.saveCodeinPref(getContext(),String.valueOf(n));
        }

        textViewcode.setText(codebutoff);

        resetbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int n2 = 10000 + new Random().nextInt(90000);
                PreferencesConfig.saveCodeinPref(getContext(),String.valueOf(n2));
                codebutoff = PreferencesConfig.loadCodefromPref(getActivity());
                textViewcode.setText(codebutoff);
                mDatabase.child(getString(R.string.app_name)).child(codebutoff).removeValue();
            }
        });

        linkbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child(getString(R.string.app_name)).child(codebutoff).removeValue();
                String edcode = linket.getText().toString();
                PreferencesConfig.saveCodeinPref(getContext(),edcode);
                codebutoff = PreferencesConfig.loadCodefromPref(getActivity());
                textViewcode.setText(codebutoff);
            }
        });
        return view;
    }


}
