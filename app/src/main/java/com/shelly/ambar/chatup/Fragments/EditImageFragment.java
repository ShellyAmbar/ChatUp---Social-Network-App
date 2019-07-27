package com.shelly.ambar.chatup.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.google.firebase.auth.FirebaseAuth;
import com.shelly.ambar.chatup.Interfaces.EditImageFragmentListener;
import com.shelly.ambar.chatup.R;

public class EditImageFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

    EditImageFragmentListener listener;
    SeekBar seekBar_Brightness,seekBar_Contrast,seekBar_Saturetion;

    public void setListener(EditImageFragmentListener listener) {
        this.listener = listener;
    }

    public EditImageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(FirebaseAuth.getInstance()==null){
            getFragmentManager().beginTransaction().remove(this).commit();
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView= inflater.inflate(R.layout.fragment_edit_image, container, false);

        seekBar_Brightness=itemView.findViewById(R.id.seekBar_Brightness);
        seekBar_Contrast=itemView.findViewById(R.id.seekBar_Constraint);
        seekBar_Saturetion=itemView.findViewById(R.id.seekBar_Saturation);

        seekBar_Brightness.setMax(200);
        seekBar_Brightness.setProgress(100);

        seekBar_Contrast.setMax(20);
        seekBar_Contrast.setProgress(0);

        seekBar_Saturetion.setMax(30);
        seekBar_Saturetion.setProgress(10);

        seekBar_Saturetion.setOnSeekBarChangeListener(this);
        seekBar_Contrast.setOnSeekBarChangeListener(this);
        seekBar_Brightness.setOnSeekBarChangeListener(this);


        return itemView;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (listener!=null){
            if(seekBar.getId()==R.id.seekBar_Brightness){
                listener.onBrightnessChanged(progress-100);

            }else if(seekBar.getId()==R.id.seekBar_Constraint){
                float Valu= .10f*progress;
                listener.onConstrantChanged(Valu);

            }else if(seekBar.getId()==R.id.seekBar_Saturation){
                float Valu= .10f*progress;
                listener.onSaturationChanged(Valu);

            }
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        listener.onEditStart();

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (listener != null){
            listener.onEditComplete();
        }

    }

    public void resetControls(){
        seekBar_Brightness.setProgress(100);
        seekBar_Contrast.setProgress(0);
        seekBar_Saturetion.setProgress(10);
    }
}
