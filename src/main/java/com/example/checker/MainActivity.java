package com.example.checker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.Menu;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.MenuItem;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.io.File;

public class MainActivity extends AppCompatActivity {
    //list of voc
    public static ArrayList<Pair<String,String>> allVOC = new ArrayList<Pair<String,String>>(){};
    //favourite system
    public static ArrayList<Pair<String,String>> favourites = new ArrayList<Pair<String,String>>(){};
    public static ArrayList<Integer> favouritesOrder = new ArrayList<Integer>(){};
    private static Pair<String,String> currentPair = new Pair<>("test","test");
    private static boolean currentPairIsFavourite = false;
    //category selection
    public static ArrayList<Pair<String,String>> VOCofCurrentCategory = new ArrayList<Pair<String,String>>(){};
    public static String currentCategory = "ZUFALL";
    public static ArrayList<Integer> vocabOrder = new ArrayList<Integer>(){};
    public static int categoryCounter = 0;
    //"swap languages" button
    public static boolean swapLanguages = false;
    public static boolean goPressed = false;
    //read in txt file
    private static String vocString = "";
    private static String[] lines = {};
    private static ArrayList<Pair<String,String>> allVOCInRandomOrder = new ArrayList<Pair<String,String>>(){};
    private static int allRandomCounter = 0;
    private static boolean isQuestion = true;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //allow type-to-search functionality when using keyboards
        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);

        //initialize views
        final TextView displayQuestion = findViewById(R.id.displayQuestion);
        final TextView displayAnswer = findViewById(R.id.displayAnswer);
        final TextView displayCategory = findViewById(R.id.displayCategory);
        final FloatingActionButton settingsButton = findViewById(R.id.settingsButton);
        final ImageButton searchButton = findViewById(R.id.searchButton);
        final ImageButton favouriteButton = findViewById(R.id.favouriteButton);
        //set font
        final Typeface futura_medium = Typeface.createFromAsset(getAssets(),  "fonts/futura_medium.ttf");
        displayQuestion.setTypeface(futura_medium);
        displayAnswer.setTypeface(futura_medium);
        displayCategory.setTypeface(futura_medium);




        //READ VOCABULARY FROM TXT FILE
        //import from the included vocabulary list
        importVocFromTxt("voc/voc.txt");
        //create File on Internal Storage for Users to add Vocabulary, import from it
        String internalStoragePath = getFilesDir().getAbsolutePath();
        File externalVocFile = new File(internalStoragePath,"checkerVocabulary.txt");
        try{ externalVocFile.createNewFile();}catch(Exception e){e.printStackTrace();}
        importVocFromTxt(externalVocFile.getAbsolutePath());

        //create another VOC list in random order for the "ZUFALL" category (random order for other categories is handled upon category selection)
        //PASS A COPY, NOT A POINTER! copy byval
        allVOCInRandomOrder = randomizedVOCOrder((ArrayList<Pair<String, String>>)allVOC.clone());

        //restore favourites from preferences (persistence)
        restoreFavourites();

        //CREATE AND SET DROPDOWN MENU
        createMenu();

        //REACT TO "SEARCH BUTTON" PRESS (search is then handled in SearchableActivity, as declared in the Manifest.xml)
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onSearchRequested();
            }});


        //REACT TO "SETTINGS" BUTTON
        settingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent settingsIntent = new Intent(getApplicationContext(), Settings.class);
                MainActivity.this.startActivity(settingsIntent);
            }});

        //REACT TO "FAVOURITE" BUTTON
        favouriteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(goPressed     //prevent making starting screen a favourite -> null pointer exception
                && !(currentCategory.equals("FAVORITEN")&&favourites.isEmpty()) ){     //prevent making "no favourites yet"-screen a
                    currentPairIsFavourite=favourites.contains(currentPair);
                    if(currentPairIsFavourite){ currentPairIsFavourite=false; //if favourite was selected, deselect
                        favouriteButton.setImageResource(android.R.drawable.btn_star_big_off);
                        try{favourites.remove(currentPair);}catch(Exception e){e.printStackTrace();
                        isQuestion=true;}
                        //adjust randomOrder to fit smaller ArrayList size
                        SpinnerActivity.favouritesRandomizeOrder();
                    }
                    else{currentPairIsFavourite=true;   //if favourite was not selected, select it
                        favouriteButton.setImageResource(android.R.drawable.btn_star_big_on);
                        try{favourites.add(currentPair);}catch(Exception e){e.printStackTrace();}
                        //adjust randomOrder to fit smaller ArrayList size
                        SpinnerActivity.favouritesRandomizeOrder();
                    }
                    saveFavourites();
                }
            }});

        //REACT TO "GO!" BUTTON PRESS
        final Button button = findViewById(R.id.goButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goPressed = true;
                Random random = new Random();



                switch(currentCategory){
                    case "ZUFALL":
                        //Category All - choose random vocab
                        //if allRandomCounter reaches the end of the allRandomVOC ArrayList, it shall loop around to 0
                        if(allRandomCounter == allVOCInRandomOrder.size()){allRandomCounter=0;}

                        if(isQuestion){
                            currentPair=allVOCInRandomOrder.get(allRandomCounter);
                            displayAnswer.setText("");
                            displayCategory.setText(currentCategory);
                            displayQuestion.setText(currentPair.second.split("#")[swapLanguages ?0:1]);
                            isQuestion=false;
                            favouriteButton.setImageResource(favourites.contains(currentPair)?android.R.drawable.btn_star_big_on:android.R.drawable.btn_star_big_off);
                        }else{
                            displayAnswer.setText(currentPair.second.split("#")[swapLanguages ?1:0]);
                            isQuestion=true;
                            allRandomCounter ++;
                        }
                        break;
                    case "FAVORITEN":   //favourite category was selected
                        int i = 0;  //TODO debug
                        if(!favourites.isEmpty()){  //prevent null pointer exception when entering an empty favourites category
                            //if categoryCounter reaches end of current category, reset to zero
                            if(categoryCounter ==favourites.size()){
                                categoryCounter =0;}

                            if(isQuestion){
                                currentPair=favourites.get(favouritesOrder.get(categoryCounter));
                                displayAnswer.setText("");
                                displayCategory.setText("FAVORITEN");
                                displayQuestion.setText(currentPair.second.split("#")[swapLanguages ?0:1]);
                                isQuestion=false;
                                favouriteButton.setImageResource(favourites.contains(currentPair)?android.R.drawable.btn_star_big_on:android.R.drawable.btn_star_big_off);
                            }else{
                                displayAnswer.setText(currentPair.second.split("#")[swapLanguages ?1:0]);
                                isQuestion=true;
                                categoryCounter++;
                            }
                        }else{  //executes when there are no favourites yet
                            displayCategory.setText("FAVORITEN");
                            displayQuestion.setText("keine Favoriten vorhanden");
                            displayAnswer.setText("");
                            favouriteButton.setImageResource(android.R.drawable.btn_star_big_off);
                            isQuestion=true;
                        }
                        break;
                    default:        //some specific category was selected
                        //if categoryCounter reaches end of current category, reset to zero
                        if(categoryCounter ==VOCofCurrentCategory.size()){
                            categoryCounter =0;}

                        //the spinner handles providing the vocabulary from the selected category (VOCofCurrentCategory) as well as
                        //a random order in which to display that vocabulary in form of a list of Integers to cycle through, stating
                        //the index of the entry to show (vocabOrder)

                        if(isQuestion){
                            currentPair=VOCofCurrentCategory.get(vocabOrder.get(categoryCounter));
                            displayAnswer.setText("");
                            displayCategory.setText(currentCategory);
                            displayQuestion.setText(currentPair.second.split("#")[swapLanguages ?0:1]);
                            isQuestion=false;
                            favouriteButton.setImageResource(favourites.contains(currentPair)?android.R.drawable.btn_star_big_on:android.R.drawable.btn_star_big_off);
                        }else{
                            displayAnswer.setText(currentPair.second.split("#")[swapLanguages ?1:0]);
                            isQuestion=true;
                            categoryCounter++;
                        }
                        break;
                }
            }
        });






    }


    private void importVocFromTxt(String filePathToTxt){
        try{
            AssetManager assetManager = getAssets();
            InputStream vocFile = assetManager.open(filePathToTxt);
            //snippet to convert voc file to string object
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = vocFile.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            // StandardCharsets.UTF_8.name() > JDK 7
            vocString = result.toString("UTF-8");

            //convert string to Arraylist of <Category,Question#Answer> pairs
            lines = vocString.replaceAll("\r","").split("\n");
            String currentCategory = "test";
            for (int i=0; i<lines.length;i++) {
                if(lines[i].startsWith("%")){
                    currentCategory  = lines[i].replace("%","");
                }
                else{
                    //check if line has correct format
                    if(lines[i].contains("#") && lines[i].split("#")[0] != null && lines[i].split("#")[1] != null)
                    //add line to allVOC if not already existent
                    {
                        if(!allVOC.contains(new Pair<>(currentCategory,lines[i])))
                        {allVOC.add(new Pair<>(currentCategory,lines[i]));}
                    }
                }
            }
        }
        catch(Exception e){e.printStackTrace();}
    }

    //should be called after all imports have been done,
    //otherwise the newly imported vocabulary will only show up in the "Zufall" category
    private void createMenu(){
        try {
            //create and set menu
            Spinner spinner = (Spinner) findViewById(R.id.categoryMenu);
            //collect category names from allVOC and convert them to a ArrayList of Strings
            ArrayList<String> listOfCategories = new ArrayList<String>() {
            };
            for (Pair<String, String> p : allVOC) {
                if (!listOfCategories.contains(p.first)) {
                    listOfCategories.add(p.first);
                }
            }

            //add all the Category names in the ArrayList to the Menu Adapter
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.a_layout_file);
            adapter.add("ZUFALL");
            adapter.add("FAVORITEN");
            for (String s : listOfCategories) {
                adapter.add(s);
            }

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new SpinnerActivity());


        }catch(Exception e){e.printStackTrace();}
   }

    private ArrayList<Pair<String,String>> randomizedVOCOrder(ArrayList<Pair<String,String>> oldOrder){
       ArrayList<Integer> order = new ArrayList<Integer>(){};
       //create random list of indexes
       Random random = new Random();
       while(order.size()<oldOrder.size()){
           int r = random.nextInt(oldOrder.size());
           if(!order.contains(r)){order.add(r);}
       }
       //add vocabulary from oldOrder at random indexes to newOrder according to the order-List of Integers
       ArrayList<Pair<String, String>> newOrder = new ArrayList<Pair<String, String>>();
       for (Integer i:order) {
           newOrder.add(oldOrder.get(i));
       }

       //return the new, randomized order
        return newOrder;
   }


    //serialize the favourites list for and add them to preferences for persistence (call before exit/destroy)
   private void saveFavourites(){
       SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
       SharedPreferences.Editor editor = preferences.edit();
       editor.clear();  //risky: if application shuts down before apply() call below, data will be lost.
       for (Pair p:favourites) {
           editor.putString(p.second.toString(),p.first+"%"+p.second);
       }
       editor.apply();
   }

   private void restoreFavourites(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Map<String, ?> map = preferences.getAll();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
                Pair<String,String> p = new Pair<>(entry.getValue().toString().split("%")[0],entry.getValue().toString().split("%")[1]);
                favourites.add(p);
        }
    }

   //override all closing events to save favourites to memory before exiting app
    @Override
    protected void onPause(){
        super.onPause();
        saveFavourites();
    }
    @Override
    protected void onStop(){
        super.onStop();
        saveFavourites();
    }







}

