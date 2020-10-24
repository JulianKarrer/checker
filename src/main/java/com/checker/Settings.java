package com.checker;

import android.graphics.Typeface;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.checker.R;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        final Switch swapLanguagesSwitch = findViewById(R.id.swapLanguagesSwitch);

        //set font
        final Typeface futura_medium = Typeface.createFromAsset(getAssets(),  "fonts/futura_medium.ttf");
        final TextView creditsAndCopyright = findViewById(R.id.creditsAndCopyright);
        creditsAndCopyright.setTypeface(futura_medium);
        creditsAndCopyright.setLineSpacing(0,2);

        //SWAP LANGUAGES BUTTON
        //initiate "swap languages" switch to the state of the swapLanguages variable in MainActivity
        //this is necessary because the state of the switch is reset when the Settings window is closed.
        swapLanguagesSwitch.setChecked(MainActivity.swapLanguages);
        //react to the "swap languages" switch being switched
        swapLanguagesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MainActivity.swapLanguages = swapLanguagesSwitch.isChecked(); }
        });





    }



}
