package com.sunilpaulmathew.researchgateclient;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.sunilpaulmathew.researchgateclient.fragments.ResearchGateFragment;
import com.sunilpaulmathew.researchgateclient.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 12, 2021
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize App Theme
        Utils.initializeAppTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new ResearchGateFragment()).commit();
    }

}