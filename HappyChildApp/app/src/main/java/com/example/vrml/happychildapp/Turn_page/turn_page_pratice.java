package com.example.vrml.happychildapp.Turn_page;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by VRML on 2017/3/7.
 */

public class turn_page_pratice extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new turnView(this));
    }
}
