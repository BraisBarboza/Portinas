package com.example.portinas;


import static com.example.portinas.CodeFragment.codebutoff;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EnterAforoActivity extends AppCompatActivity {


    public static String KEY_TEXT = "0568";
    String aforo_total;
    String boot = "Executed";
    @BindView(R.id.square_bt)
    Button Continuebut;
    @BindView(R.id.square_edtxt)
    EditText aforoedtxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences("BOOT_PREF",MODE_PRIVATE);
        boolean notfirstboot = preferences.getBoolean(boot,false);

        setTheme(R.style.Theme_Portinas);
        if (!notfirstboot) {
            setContentView(R.layout.activity_enteraforo);
            ButterKnife.bind(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(boot,true);
            editor.apply();
        } else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }

    @OnClick(R.id.square_bt)
    public void submit() {
        String aforonum = aforoedtxt.getText().toString();
        if (!aforonum.isEmpty()) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra(KEY_TEXT, aforonum);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.texto_vacio_str), Toast.LENGTH_SHORT).show();
        }
    }

}