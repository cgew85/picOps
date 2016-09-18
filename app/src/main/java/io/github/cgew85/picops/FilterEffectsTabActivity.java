package io.github.cgew85.picops;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class FilterEffectsTabActivity extends TabActivity {
    private static final String FILTER_SPEC = "filter";
    private static final String EFFECTS_SPEC = "effects";
    private int fragmentWidth, fragmentHeight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** Ausblenden der Action- und der Statusbar **/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.filtereffectstabhost);

        /** Daten aus aufrufendem Intent zwischenspeichern **/
        fragmentWidth = getIntent().getIntExtra("fragmentWidth", 0);
        fragmentHeight = getIntent().getIntExtra("fragmentHeight", 0);
        Log.d("INFO", "Daten aus Intent in TabHost");
        Log.d("INFO", "fragmentWidth: " + fragmentWidth);
        Log.d("INFO", "fragmentHeight: " + fragmentHeight);

        TabHost tabHost = getTabHost();

        tabHost.getTabWidget().setBackgroundColor(Color.BLACK);

        TabSpec filterSpec = tabHost.newTabSpec(FILTER_SPEC);
        filterSpec.setIndicator(FILTER_SPEC);
        Intent filterintent = new Intent(this, FilterTabActivity.class);
        filterintent.putExtra("fragmentWidth", fragmentWidth);
        filterintent.putExtra("fragmentHeight", fragmentHeight);
        filterSpec.setContent(filterintent);

        TabSpec effectSpec = tabHost.newTabSpec(EFFECTS_SPEC);
        effectSpec.setIndicator(EFFECTS_SPEC);
        Intent effectintent = new Intent(this, EffectsTabActivity.class);
        effectintent.putExtra("fragmentWidth", fragmentWidth);
        effectintent.putExtra("fragmentHeight", fragmentHeight);
        effectSpec.setContent(effectintent);

        tabHost.addTab(filterSpec);
        tabHost.addTab(effectSpec);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("INFO", "Back pressed in TabHost");
        Intent intent = new Intent(this, BearbeitungsActivity.class);
        startActivity(intent);
    }


}
