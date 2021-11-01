package com.example.portinas;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private int progr_num = 0;
    private int progr_per = 0;
    private int aforo_total = 0;
    @BindView(R.id.progress_bar_txt)
    TextView progress_tv;
    @BindView(R.id.button_increase)
    Button but_increment;
    @BindView(R.id.button_decrease)
    Button but_decrement;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String aforo_num = bundle.getString(EnterAforoActivity.KEY_TEXT);
            aforo_total = Integer.parseInt(aforo_num);
            progress_tv.setText(progr_num + "/" + aforo_num);
            progressBar.setMax(aforo_total);
        }

    }

    @OnClick(R.id.button_increase)
    public void submitButtonIncrease(){
        if (progr_num < aforo_total){
            progr_num += 1;
            updateProgressBar();
        }
    }

    @OnClick(R.id.button_decrease)
    public void submitButtonDecrease(){
        if (progr_num > 0){
            progr_num -= 1;
            updateProgressBar();
        }
    }

    private void updateProgressBar(){
        progressBar.setProgress(progr_num);
        progress_tv.setText(progr_num + "/" + aforo_total);
    }
}
