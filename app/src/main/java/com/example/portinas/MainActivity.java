package com.example.portinas;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionBarContextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private int progr_num = 0;
    private int progr_per = 0;
    private int aforo_total = 0;
    private ActionBarDrawerToggle toggle;
    @BindView(R.id.progress_bar_txt)
    TextView progress_tv;
    @BindView(R.id.button_increase)
    Button but_increment;
    @BindView(R.id.button_decrease)
    Button but_decrement;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String aforo_num = bundle.getString(EnterAforoActivity.KEY_TEXT);
            aforo_total = Integer.parseInt(aforo_num);
            progress_tv.setText(progr_num + "/" + aforo_num);
            progressBar.setMax(aforo_total);

        }
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        toggle.syncState();

    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @OnClick(R.id.button_increase)
    public void submitButtonIncrease() {
        if (progr_num < aforo_total) {
            progr_num += 1;
            updateProgressBar();
        }
    }

    @OnClick(R.id.button_decrease)
    public void submitButtonDecrease() {
        if (progr_num > 0) {
            progr_num -= 1;
            updateProgressBar();
        }
    }

    private void updateProgressBar() {
        progressBar.setProgress(progr_num);
        progress_tv.setText(progr_num + "/" + aforo_total);
    }
}
