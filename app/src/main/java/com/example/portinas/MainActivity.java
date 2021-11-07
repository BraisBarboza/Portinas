package com.example.portinas;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionBarContextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.google.android.material.navigation.NavigationView;

import java.text.MessageFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnItemSelected;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AforoFragment.onFragmentInterface {

    private ActionBarDrawerToggle toggle;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    private int final_value = 0;
    private AforoFragment mMyFragment;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        navigationView.setNavigationItemSelectedListener(this);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, new AforoFragment());
        fragmentTransaction.commit();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String aforo_num = bundle.getString(EnterAforoActivity.KEY_TEXT);
            final_value = Integer.parseInt(aforo_num);
        }
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        toggle.syncState();
        navigationView.setCheckedItem(R.id.nav_aforo);
        if (savedInstanceState != null) {
            //Restore the fragment's instance
            mMyFragment = (AforoFragment) getSupportFragmentManager().getFragment(savedInstanceState, "myFragmentName");

        }
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }
/*
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

 */

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "myFragmentName", mMyFragment);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_aforo:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AforoFragment()).commitNow();
                break;
            case R.id.nav_code:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new CodeFragment()).commitNow();
                break;
            case R.id.nav_bluetooth:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new BluetoothFragment()).commit();
                break;
            case R.id.nav_nfc:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new NFCFragment()).commit();
                break;
            case R.id.nav_giticon:
                Uri uri = Uri.parse("https://github.com/BraisBarboza/Portinas/wiki");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(Intent.createChooser(intent, getString(R.string.browse_str)));
                break;
            case R.id.nav_settings:
                break;
            case R.id.nav_compartir:
                Intent intentSend = new Intent(Intent.ACTION_SEND);
                intentSend.setType("text/plain");
                intentSend.putExtra( Intent.EXTRA_TEXT,getString(R.string.share_text));
                startActivity(intentSend);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public int onButtonIncrease(int progr_num, int aforo_total) {
        if (progr_num < aforo_total) {
            progr_num += 1;
        }
        return progr_num;
    }

    @Override
    public int onButtonDecrease(int progr_num, int aforo_total) {
        if (progr_num > 0) {
            progr_num -= 1;
        }
        return  progr_num;
    }

    @Override
    public int getAforo() {
        return final_value;
    }


}
