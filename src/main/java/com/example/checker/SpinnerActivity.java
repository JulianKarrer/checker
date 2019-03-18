package com.example.checker;

import android.widget.AdapterView;

import android.view.View;
import android.widget.AdapterView.OnItemSelectedListener;
import android.util.Pair;
import java.util.Random;

public class SpinnerActivity implements OnItemSelectedListener {
    public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
        //select new category
        MainActivity.currentCategory =  parent.getItemAtPosition(pos).toString();

        //count number of entries for selected category
        //add them to VOCofCurrentCategory list
        int noOfVocInCategory = 0;
        MainActivity.VOCofCurrentCategory.clear();
        for (Pair<String,String> p:MainActivity.allVOC) {
            if (p.first.equals(parent.getItemAtPosition(pos).toString())) {
                MainActivity.VOCofCurrentCategory.add(p);
                noOfVocInCategory++;
            }
        }
        //create random order for the category to be cycled through
        MainActivity.vocabOrder.clear();
        Random random = new Random();
        while(MainActivity.vocabOrder.size()<noOfVocInCategory) {
            int r = random.nextInt(noOfVocInCategory);
            if (!MainActivity.vocabOrder.contains(r)) {
                MainActivity.vocabOrder.add(r);
            }
        }

        //reset the categoryCounter
        MainActivity.categoryCounter =0;



    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
}
