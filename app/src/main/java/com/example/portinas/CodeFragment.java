package com.example.portinas;

import static com.example.portinas.MainActivity.mDatabase;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Random;

public class CodeFragment extends Fragment  {

    public static String codebutoff, codebuton = "";
    private TextView textViewcode;
    Button resetbut, linkbut;
    EditText linket;
    private CodeFragment.onCodeInterface listener;
    private static  int linked =0;
    private Context context;
    private static int reset = 2;


    public interface onCodeInterface{
        public void createDatabase();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_code, container, false);
        textViewcode = view.findViewById(R.id.code);
        resetbut = view.findViewById(R.id.resetcode_but);
        linkbut = view.findViewById(R.id.link_but);
        linket = view.findViewById(R.id.link_et);

        codebutoff = PreferencesConfig.loadCodefromPref(context);

        textViewcode.setText(codebutoff);

        resetbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (linked != 1) {
                    mDatabase.child(getString(R.string.app_name)).child(codebutoff).removeValue();
                    linked = 0;
                }
                int n2 = 10000 + new Random().nextInt(90000);
                PreferencesConfig.saveCodeinPref(context,String.valueOf(n2));
                codebutoff = PreferencesConfig.loadCodefromPref(context);
                textViewcode.setText(codebutoff);
                listener.createDatabase();
                reset = 1;


            }
        });

        linkbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String edcode = linket.getText().toString();
                reset = 0;
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() && snapshot.child(context.getString(R.string.app_name)).hasChild(edcode.toString()) && (!edcode.equals(""))) {
                            PreferencesConfig.saveCodeinPref(context,edcode);
                            codebutoff = PreferencesConfig.loadCodefromPref(context);
                            textViewcode.setText(codebutoff);
                            //close_keyboard();
                            linked = 1;
                        } else {
                            if ( reset  == 0)
                            Toast.makeText(context,context.getString(R.string.insertcode) + edcode,Toast.LENGTH_SHORT).show();
                            reset = 1;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                codebutoff = PreferencesConfig.loadCodefromPref(context);
                textViewcode.setText(codebutoff);
            }

        });
        return view;
    }

    /*private void close_keyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }*/

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            this.context = context;
            listener = (CodeFragment.onCodeInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " implement createDatabase");
        }

    }




}
