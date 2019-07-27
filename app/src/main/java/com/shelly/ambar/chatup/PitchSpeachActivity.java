package com.shelly.ambar.chatup;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class PitchSpeachActivity extends AppCompatActivity {
    private EditText TextToSpeechEditText;
    private SeekBar pitch_seekbar;
    private SeekBar speed_seekbar;
    private int pitch_seekbar_value;
    private int speed_seekbar_value;
    private TextView pitch_value_text;
    private TextView speed_value_text;
    private Button speekButton;
    private Toolbar toolbar;
    private android.speech.tts.TextToSpeech mTTs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pitch_speach);
        TextToSpeechEditText=findViewById(R.id.TextToSpeech);
        pitch_seekbar=findViewById(R.id.pitch_seekbar);
        speed_seekbar=findViewById(R.id.speed_seekbar);
        pitch_value_text=findViewById(R.id.pitch_value_text);
        speed_value_text=findViewById(R.id.speed_value_text);
        speekButton=findViewById(R.id.speekButton);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Text to speech");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();

            }
        });
        toolbar.setNavigationIcon(ContextCompat.getDrawable(PitchSpeachActivity.this,R.drawable.ic_goback));
        pitch_seekbar_value=50;
        speed_seekbar_value=50;

        Intent intent=getIntent();
        String text=intent.getStringExtra("text");

        TextToSpeechEditText.setText(text);
        pitch_seekbar.setMax(100);
        speed_seekbar.setMax(100);

        pitch_seekbar.setProgress(pitch_seekbar_value);
        speed_seekbar.setProgress(speed_seekbar_value);
        pitch_value_text.setText(""+ pitch_seekbar_value);
        speed_value_text.setText(""+ speed_seekbar_value);

        pitch_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                pitch_seekbar_value=progress;
                pitch_seekbar.setProgress(pitch_seekbar_value);
                pitch_value_text.setText(""+ pitch_seekbar_value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        speed_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speed_seekbar_value=progress;
                speed_seekbar.setProgress(speed_seekbar_value);
                speed_value_text.setText(""+speed_seekbar_value);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mTTs=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status==TextToSpeech.SUCCESS){
                    int result= mTTs.setLanguage(Locale.ENGLISH);
                    if(result==TextToSpeech.LANG_MISSING_DATA
                            || result==TextToSpeech.LANG_NOT_SUPPORTED){

                        Log.e("TTS","language not supported");
                        Toast.makeText(PitchSpeachActivity.this, "language not supported"
                                , Toast.LENGTH_SHORT).show();

                    }else{
                        speekButton.setEnabled(true);
                    }

                }else{
                    Log.e("TTS","Initialization Failed");
                    Toast.makeText(PitchSpeachActivity.this
                            , "Initialization Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        speekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speekNow();
            }
        });


    }

    private void speekNow() {
        String text=TextToSpeechEditText.getText().toString();
        float pitchValue=(float) pitch_seekbar.getProgress()/50;
        if(pitchValue<0.1f)pitchValue=0.1f;
        float speedValue=(float) speed_seekbar.getProgress()/50;
        if(speedValue<0.1f)speedValue=0.1f;

        mTTs.setPitch(pitchValue);
        mTTs.setSpeechRate(speedValue);

        mTTs.speak(text,TextToSpeech.QUEUE_FLUSH,null);

    }

    @Override
    protected void onDestroy() {
        if(mTTs!=null){
            mTTs.stop();
            mTTs.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if(mTTs!=null){
            mTTs.stop();
            mTTs.shutdown();
        }
        finish();
    }
}
