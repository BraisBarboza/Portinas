package com.example.portinas;


import static com.example.portinas.CodeFragment.codebutoff;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AforoFragment.onFragmentInterface, CodeFragment.onCodeInterface {
    String AFORO_FRAGMENT_KEY="AFORO_FRAGMENT_KEY";
    private ActionBarDrawerToggle toggle;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    private int final_value = 0;
    private Fragment aforo_fragment;
    public static DatabaseReference mDatabase;
    String mGroupId,codebutoff;
    private int n = 10000 + new Random().nextInt(90000);
    private long backPressedTime;
    private  Toast backToast;
    String boot = "Executed";


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
        fragmentTransaction.add(R.id.fragment_container, new AforoFragment(),AFORO_FRAGMENT_KEY);
        fragmentTransaction.commit();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String aforo_num = bundle.getString(EnterAforoActivity.KEY_TEXT);
            final_value = Integer.parseInt(aforo_num);
            PreferencesConfig.saveTotalinPref(getApplicationContext(), final_value);
        }
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        toggle.syncState();
        navigationView.setCheckedItem(R.id.nav_aforo);
        if (savedInstanceState != null) {
            aforo_fragment = getSupportFragmentManager().getFragment(savedInstanceState, AFORO_FRAGMENT_KEY);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> map = new HashMap<>();
        map.put("Current",0);
        map.put("Total",final_value);

        codebutoff = PreferencesConfig.loadCodefromPref(getApplicationContext());
        if (codebutoff.equals(getText(R.string.defaultValue)))
        {
            PreferencesConfig.saveCodeinPref(getApplicationContext(),String.valueOf(n));
            codebutoff = PreferencesConfig.loadCodefromPref(getApplicationContext());
        }

        SharedPreferences preferences = getSharedPreferences("BOOT_PREF",MODE_PRIVATE);
        boolean notfirstboot = preferences.getBoolean(boot,false);
        if (!notfirstboot) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(boot,true);
            editor.apply();
            mDatabase.child(getString(R.string.app_name)).child(codebutoff).setValue(map);
        }


    }
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (backPressedTime + 2000 > System.currentTimeMillis()) {
                backToast.cancel();
                super.onBackPressed();
                return;
            } else {
                backToast = Toast.makeText(getBaseContext(), getText(R.string.backpress), Toast.LENGTH_SHORT);
                backToast.show();
            }
            backPressedTime = System.currentTimeMillis();
        }

    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, AFORO_FRAGMENT_KEY, aforo_fragment);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        aforo_fragment=getSupportFragmentManager().findFragmentByTag(AFORO_FRAGMENT_KEY);
        switch (item.getItemId()) {
            case R.id.nav_aforo:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, aforo_fragment).commitNow();
                break;
            case R.id.nav_code:
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                        new CodeFragment()).commitNow();
                break;
            case R.id.nav_nfc:
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                        new NFCFragment()).commit();
                break;
            case R.id.nav_giticon:
                Uri uri = Uri.parse("https://github.com/BraisBarboza/Portinas/wiki");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(Intent.createChooser(intent, getString(R.string.browse_str)));
                break;
            case R.id.nav_settings:
                Intent intentsetting = new Intent(this, SettingsActivity.class);
                startActivity(intentsetting);

                break;
            case R.id.nav_compartir:
                Intent intentSend = new Intent(Intent.ACTION_SEND);
                intentSend.setType("text/plain");
                intentSend.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text));
                startActivity(intentSend);
                break;
            default:
                return true;
        }
        
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public int onButtonIncrease(int progr_num, int aforo_total) {
        if (progr_num < aforo_total) {
            progr_num += 1;
        }
        codebutoff = PreferencesConfig.loadCodefromPref(getApplicationContext());
        mDatabase.child(getString(R.string.app_name)).child(codebutoff).child("Current").setValue(progr_num);
        return progr_num;
    }

    @Override
    public int onButtonDecrease(int progr_num, int aforo_total) {
        if (progr_num > 0) {
            progr_num -= 1;
        }
        codebutoff = PreferencesConfig.loadCodefromPref(getApplicationContext());
        mDatabase.child(getString(R.string.app_name)).child(codebutoff).child("Current").setValue(progr_num);
        return progr_num;
    }

    @Override
    public int getAforo() {
        return Integer.valueOf(PreferencesConfig.loadTotalfromPref(getApplicationContext()));
    }

    @Override
    public void refreshProgressBar(ProgressBar progressBar, TextView textView) {
        progressBar.setProgress(0);
        String total = PreferencesConfig.loadTotalfromPref(getApplicationContext()).toString();
        textView.setText(0 + "/" + total);
    }

    @Override
    public void createDatabase() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("Current",0);
        map.put("Total",final_value);
        codebutoff = PreferencesConfig.loadCodefromPref(getApplicationContext());
        mDatabase.child(getString(R.string.app_name)).child(codebutoff).setValue(map);
    }
}
