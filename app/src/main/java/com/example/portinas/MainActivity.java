package com.example.portinas;


import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
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


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AforoFragment.onFragmentInterface, CodeFragment.onCodeInterface, NFCFragment.onNFCFragmentInterface {
    String AFORO_FRAGMENT_KEY = "AFORO_FRAGMENT_KEY";
    private ActionBarDrawerToggle toggle;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    private int final_value = 0;
    private Fragment aforo_fragment;
    public static DatabaseReference mDatabase;
    String mGroupId, codebutoff;
    private int n = 10000 + new Random().nextInt(90000);
    private long backPressedTime;
    private Toast backToast;
    String boot = "Executed";
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter writingTagFilters[];
    boolean writeMode;
    Tag myTag;
    int logged, current;
    String tag;
    //TODO: Meter los tags NFC en firebase
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
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        navigationView.setNavigationItemSelectedListener(this);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, new AforoFragment(), AFORO_FRAGMENT_KEY);
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
        map.put("Current", 0);
        map.put("Total", final_value);


        codebutoff = PreferencesConfig.loadCodefromPref(getApplicationContext());
        if (codebutoff.equals(getText(R.string.defaultValue))) {
            PreferencesConfig.saveCodeinPref(getApplicationContext(), String.valueOf(n));
            codebutoff = PreferencesConfig.loadCodefromPref(getApplicationContext());
        }

        SharedPreferences preferences = getSharedPreferences("BOOT_PREF", MODE_PRIVATE);
        boolean notfirstboot = preferences.getBoolean(boot, false);
        if (!notfirstboot) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(boot, true);
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
        aforo_fragment = getSupportFragmentManager().findFragmentByTag(AFORO_FRAGMENT_KEY);
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
                String aux= PreferencesConfig.loadTotalfromPref(this);
                mDatabase.child(getString(R.string.app_name)).child(codebutoff).child("Total").setValue(Integer.valueOf(aux));

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
        } else {
            Toast.makeText(getApplicationContext(),getString(R.string.full_str),Toast.LENGTH_SHORT).show();
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
        return Integer.parseInt(PreferencesConfig.loadTotalfromPref(this));

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
        map.put("Current", 0);
        map.put("Total", final_value);
        codebutoff = PreferencesConfig.loadCodefromPref(getApplicationContext());
        mDatabase.child(getString(R.string.app_name)).child(codebutoff).setValue(map);
    }

    @Override
    public String onWriteNFCPressed(String string) {
        String text;
        try {
            if (myTag == null) {
                Toast.makeText(getApplicationContext(), getString(R.string.nfc_error_detected), Toast.LENGTH_SHORT).show();
            } else {
                writeNFC(string, myTag);
                Toast.makeText(getApplicationContext(), getString(R.string.nfc_write_success), Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), getString(R.string.nfc_write_error), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (FormatException e) {
            Toast.makeText(getApplicationContext(), getString(R.string.nfc_write_error), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, getString(R.string.no_nfc_toast_text), Toast.LENGTH_LONG).show();
        }else {
            text = readFromIntent(getIntent());
            IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
            tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
            writingTagFilters = new IntentFilter[]{tagDetected};
            return text;
        }
        return "";
    }

    public String readFromIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action) || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action) || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs = null;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            }
            return buildTagViews(msgs);
        }
        return null;
    }

    private String buildTagViews(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0) return null;
        String text = "";
        byte[] payload = msgs[0].getRecords()[0].getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
        int languageCodeLength = payload[0] & 0063;
        try {
            text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);

        } catch (UnsupportedEncodingException e) {
            Log.e("UnsupportedEncoding", e.toString());
        }
        increaseAforoNFC(text);


        return text;

    }

    private void increaseAforoNFC(String ID) {
        mDatabase.child(getString(R.string.app_name)).child(codebutoff).child("Current").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    current = Integer.parseInt(String.valueOf(task.getResult().getValue()));
                    mDatabase.child(getString(R.string.app_name)).child(codebutoff).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Log.e("firebase", "Error getting data", task.getException());
                            } else {
                                if(!task.getResult().hasChild(ID)) {
                                    mDatabase.child(getString(R.string.app_name)).child(codebutoff).child(ID).setValue(1);
                                    mDatabase.child(getString(R.string.app_name)).child(codebutoff).child("Current").setValue(current + 1);
                                    return;

                                }else{
                                    mDatabase.child(getString(R.string.app_name)).child(codebutoff).child(ID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                            if (!task.isSuccessful()) {
                                                Log.e("firebase", "Error getting data", task.getException());
                                            } else {
                                                if (Integer.parseInt(String.valueOf(task.getResult().getValue())) == 1) {
                                                    mDatabase.child(getString(R.string.app_name)).child(codebutoff).child("Current").setValue(current - 1);
                                                    mDatabase.child(getString(R.string.app_name)).child(codebutoff).child(ID).setValue(0);
                                                } else {
                                                    mDatabase.child(getString(R.string.app_name)).child(codebutoff).child("Current").setValue(current + 1);
                                                    mDatabase.child(getString(R.string.app_name)).child(codebutoff).child(ID).setValue(1);
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void writeNFC(String text, Tag tag) throws IOException, FormatException {
        NdefRecord[] records = {createRecord(text)};
        NdefMessage message = new NdefMessage(records);
        Ndef ndef = Ndef.get(tag);
        ndef.connect();
        ndef.writeNdefMessage(message);
        ndef.close();
    }

    private NdefRecord createRecord(String text) throws UnsupportedEncodingException {
        String lang = "en";
        byte[] textBytes = text.getBytes();
        byte[] langBytes = lang.getBytes("US-ASCII");
        int langLength = langBytes.length;
        int textLength = textBytes.length;
        byte[] payload = new byte[1 + langLength + textLength];
        payload[0] = (byte) langLength;
        System.arraycopy(langBytes, 0, payload, 1, langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);
        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);
        return recordNFC;

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        readFromIntent(intent);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        WriteModeOn();
    }

    @Override
    protected void onPause() {
        super.onPause();
        WriteModeOff();
    }

    private void WriteModeOn() {
        writeMode = true;
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            return;
        }
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, writingTagFilters, null);
    }

    private void WriteModeOff() {
        writeMode = false;
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null) {
            return;
        }
        nfcAdapter.disableForegroundDispatch(this);
    }
}
