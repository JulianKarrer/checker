package com.checker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.checker.R;

import java.util.Map;

public class TextEditor extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_editor);

        final EditText textEditor = findViewById(R.id.textEditor);
        final Button submitButton = findViewById(R.id.submitButton);
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        textEditor.setText(preferences.getString("plaintext","loading error"));


        //when submit button is pressed, delete old textEditor voc from preferences and save new ones
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String stringFromEditor = textEditor.getText().toString();
                SharedPreferences.Editor editor = preferences.edit();
                Map<String, ?> map = preferences.getAll();

                //update plaintext
                editor.remove("plaintext");
                editor.putString("plaintext",stringFromEditor);

                //first, remove all textEditor vocabulary pairs from preferences
                for (Map.Entry<String, ?> entry : map.entrySet()) {
                    if (entry.getKey().startsWith("textEditor")){
                        editor.remove(entry.getKey());
                    }
                }

                //then, add the new vocabulary to preferences
                String[] lines = stringFromEditor.replaceAll("\r","").split("\n");
                String category = "Text Editor";
                for (String s:lines) {
                    if(s.startsWith("%")){
                        category  = s.replace("%","");
                    }
                    else{
                        //check if line has correct format
                        if(s.contains("#") && !s.endsWith("#") && !s.startsWith("#"))
                        //add line to preferences if not already existent
                        {
                            if(!preferences.contains("textEditor"+category+"%"+s))
                            {editor.putString("textEditor"+category+"%"+s,category+"%"+s);}
                        }
                    }
                }
                editor.apply();

                //return code execution to a @override onActivityResult in MainActivity, so that vocabulary can be loaded into allVoc there
                //this closes the Text Editor Activity
                Intent returnIntent = new Intent();
                returnIntent.putExtra("return",1);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }});


    }
}
