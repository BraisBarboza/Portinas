package com.example.portinas;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Random;

public class NFCFragment extends Fragment{
    private NFCFragment.onNFCFragmentInterface listener;
    Button writeButton;
    TextView NFCWriteTextView;
    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_nfc,container,false);
        writeButton = view.findViewById(R.id.NFC_button_write);
        NFCWriteTextView = view.findViewById(R.id.nfc_tv_write);
        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onWriteNFCPressed(NFCWriteTextView.getText().toString());
            }
        });
        return view;
    }
    public interface onNFCFragmentInterface{
        public void onWriteNFCPressed(String string);
    }

}
