package com.example.checker;

import android.widget.AdapterView;

import android.view.View;
import android.widget.AdapterView.OnItemSelectedListener;
import android.util.Pair;
import java.util.Random;

import static com.example.checker.MainActivity.*;

public class SpinnerActivity implements OnItemSelectedListener {
    public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
        //select new category
        currentCategory =  parent.getItemAtPosition(pos).toString();

        //reset category counter so that when already at high number, switching to a smaller category will not cause a nullPointer
        categoryCounter = 0;

        //count number of entries for selected category
        //add them to VOCofCurrentCategory list
        int noOfVocInCategory = 0;
        VOCofCurrentCategory.clear();
        for (Pair<String,String> p:allVOC) {
            if (p.first.equals(parent.getItemAtPosition(pos).toString())) {
                VOCofCurrentCategory.add(p);
                noOfVocInCategory++;
            }
        }
        if (parent.getItemAtPosition(pos).toString().equals("FAVORITEN")) {
            favouritesRandomizeOrder();
        }
        else{
            categoryRandomizeOrder(noOfVocInCategory);
        }


    }


    //create random vocabOrder in which to cycle through favourites ArrayList
    public static void favouritesRandomizeOrder(){
        Random random = new Random();
        favouritesOrder.clear();
        while (favouritesOrder.size() < favourites.size()) {
            Integer randNum = random.nextInt(favourites.size());
            if (!favouritesOrder.contains(randNum)) {
                favouritesOrder.add(randNum);
            }
        }
    }

    //create random order for the category to be cycled through
    private static void categoryRandomizeOrder(int noOfVocInCategory){
        Random random = new Random();
        vocabOrder.clear();
        while(vocabOrder.size()<noOfVocInCategory) {
            int r = random.nextInt(noOfVocInCategory);
            if (!vocabOrder.contains(r)) {
                vocabOrder.add(r);
            }
        }
    }

        //the random order for the "all" category is generated in MainActivity

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }
}
