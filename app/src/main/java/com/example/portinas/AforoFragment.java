package com.example.portinas;

import static com.example.portinas.MainActivity.mDatabase;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AforoFragment extends Fragment {
    private onFragmentInterface listener;
    String AFORO_KEY= "AFORO";
    private int currentvalue;
    private TextView progress_tv;
    private int  aforo_total = 0;
    private Button  but_increment, but_decrement;
    private ProgressBar progressBar;
    int onlineCurrent, onlineTotal;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            currentvalue=savedInstanceState.getInt(AFORO_KEY,0);
            onUpdateProgressBar(currentvalue);

        }
    }

    public interface onFragmentInterface{
        public int onButtonIncrease(int progr_num, int aforo_total);
        public int onButtonDecrease(int progr_num, int aforo_total);
        public int getAforo();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            //Restore the fragment's state here
            currentvalue = 0;
        } else {
            currentvalue =  savedInstanceState.getInt("aforo",7);
            onUpdateProgressBar(currentvalue);
        }
    }



    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(AFORO_KEY,currentvalue);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_aforo, container, false);
        progress_tv = view.findViewById(R.id.progress_bar_txt);
        but_decrement = view.findViewById(R.id.button_decrease);
        but_increment = view.findViewById(R.id.button_increase);
        progressBar = view.findViewById(R.id.progress_bar);
        aforo_total = listener.getAforo();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currentvalue= Integer.parseInt(snapshot.child("Portinas").child("Prueba").child("Current").getValue().toString());
                    aforo_total = Integer.parseInt(snapshot.child("Portinas").child("Prueba").child("Total").getValue().toString());
                    progressBar.setMax(aforo_total);
                    onUpdateProgressBar(currentvalue);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        progressBar.setMax(aforo_total);
        onUpdateProgressBar(currentvalue);


        but_increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentvalue = listener.onButtonIncrease(currentvalue,aforo_total);
                onUpdateProgressBar(currentvalue);
            }
        });

        but_decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentvalue = listener.onButtonDecrease(currentvalue,aforo_total);
                onUpdateProgressBar(currentvalue);
            }
        });
        return view;



    }
    public void onUpdateProgressBar(int value) {
        progressBar.setProgress(value);
        progress_tv.setText(value + "/" + aforo_total);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (onFragmentInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " implement onButtonIncrease & onButtonIncrease & onUpdateProgressBar");
        }

    }
}
