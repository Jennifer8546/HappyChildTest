package com.example.vrml.happychildapp;

import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import com.example.vrml.happychildapp.TurnCardGame.Turn_Card_Game;
import com.example.vrml.happychildapp.Turn_page.turn_page_pratice;

public class Choose_Mode extends AppCompatActivity {
    private SoundPool soundPool;
    private int soundID;
    DisplayMetrics metrics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choosemode);
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Button to_turncard = (Button)findViewById(R.id.to_turncard);
        Button to_teaching = (Button)findViewById(R.id.to_teaching);
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC,0);
        soundID = soundPool.load(this,R.raw.dong,0);
        if(metrics.widthPixels > 2000) {
            to_turncard.setTextSize(metrics.widthPixels / 30);
            to_teaching.setTextSize(metrics.widthPixels / 30);
        }else {
            to_turncard.setTextSize(metrics.widthPixels / 60);
            to_teaching.setTextSize(metrics.widthPixels / 60);
        }
        to_teaching.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundPool.play(soundID,1.0F,1.0F,0,0,1.0F);
                Intent intent = new Intent();
                intent.setClass(Choose_Mode.this,turn_page_pratice.class);
                startActivity(intent);
                Choose_Mode.this.finish();
            }
        });

        to_turncard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundPool.play(soundID,1.0F,1.0F,0,0,1.0F);
                Intent intent = new Intent();
                intent.setClass(Choose_Mode.this,Turn_Card_Game.class);
                startActivity(intent);
                Choose_Mode.this.finish();
            }
        });

    }
}
