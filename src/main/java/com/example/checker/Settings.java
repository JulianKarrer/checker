package com.example.checker;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //set font
        final Typeface futura_medium = Typeface.createFromAsset(getAssets(),  "fonts/futura_medium.ttf");
        final TextView creditsAndCopyright = findViewById(R.id.creditsAndCopyright);
        creditsAndCopyright.setTypeface(futura_medium);
        creditsAndCopyright.setLineSpacing(0,2);









    }



}
