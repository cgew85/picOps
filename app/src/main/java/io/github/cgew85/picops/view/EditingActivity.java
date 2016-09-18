package io.github.cgew85.picops.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import io.github.cgew85.picops.R;
import io.github.cgew85.picops.controller.*;
import io.github.cgew85.picops.model.Session;

public class EditingActivity extends FragmentActivity {
    DrawerLayout mDrawerLayout;
    ListView mDrawerList;
    ActionBarDrawerToggle mDrawerToggle;
    MenuListAdapter mMenuAdapter;
    String[] title;
    String[] subtitle;
    int[] icon;
    Fragment fragment1 = new Fragment1();
    Fragment fragment2 = new Fragment2();
    Context context = (Context) this;
    Session session = Session.getSession();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bearbeitung);

        /** Anpassen der Status- und Actionbar **/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActionBar().setBackgroundDrawable(new ColorDrawable(0x0000000));
        getActionBar().setLogo(android.R.color.transparent);
        getActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>picOps</font>"));

        /** F�llen der Listenelementnamen **/
        title = new String[]{"Edit Image", "Blend 2 Images"};
        subtitle = new String[]{"Image Editing Fragment", "Blending Fragment"};
        icon = new int[]{R.drawable.iconbrush, R.drawable.iconblend};

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mMenuAdapter = new MenuListAdapter(this, title, subtitle, icon);
        mDrawerList.setAdapter(mMenuAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }

        Intent intent = getIntent();
        String selector = intent.getStringExtra("Selector");
        if ((selector != null) && (selector.equals("Fragment2"))) {
            selectItem(1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Undo").setOnMenuItemClickListener(this.UndoButtonClickListener)
                //.setIcon(R.drawable.btnundo)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        menu.add("Save").setOnMenuItemClickListener(SaveButtonClickListener)
                //.setIcon(R.drawable.btnsave)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                mDrawerLayout.closeDrawer(mDrawerList);
            } else {
                mDrawerLayout.openDrawer(mDrawerList);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    MenuItem.OnMenuItemClickListener UndoButtonClickListener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            Toast.makeText(EditingActivity.this, "Undo Button Pressed", Toast.LENGTH_LONG).show();
            SimpleCounterForTempFileName counter = SimpleCounterForTempFileName.getInstance();
            Log.d("INFO", "Undo button pressed, counter @ " + counter.getCounter());
            Log.d("INFO", "Starting undo process");
            UndoLastStep undo = new UndoLastStep();
            undo.undo(counter.getCounter(), getApplicationContext());

            /** Letztes Element aus der Liste loeschen **/
            LogEntryListManager manager = LogEntryListManager.getInstance();
            Log.d("INFO", "Anzahl Objekte in List: " + manager.getNumberOfEntries());
            if (manager.getNumberOfEntries() > 0) {
                manager.removeLastEntry();
            }

            Intent refresh = new Intent(EditingActivity.this, EditingActivity.class);
            startActivity(refresh);
            return false;
        }
    };

    MenuItem.OnMenuItemClickListener SaveButtonClickListener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            /** ImageView leeren **/
            ImageView iv = (ImageView) findViewById(R.id.workingimageview);
            iv.setImageBitmap(null);

            Toast.makeText(EditingActivity.this, "Save Button Pressed", Toast.LENGTH_LONG).show();
            SaveImageToSD save = new SaveImageToSD();
            /** Session holen **/
            ReadWriteSettings rwSetting = ReadWriteSettings.getReadWriteSettings();

            LogEntryListManager manager = LogEntryListManager.getInstance();
            if (manager.getNumberOfEntries() > 0) {
                save.applyStepToPicture(Integer.parseInt(rwSetting.getStringSetting(context, "Session")), context);
            }
            Intent intent = new Intent(context, SelectionActivity.class);
            startActivity(intent);

            Log.d("INFO", "### Bild erstellt ###");
            manager.clearList();
            Log.d("INFO", "### LogList cleared ###");
            /** Neue Session erzeugen **/
            rwSetting = ReadWriteSettings.getReadWriteSettings();
            rwSetting.changeSetting(context, "Session", String.valueOf(session.createNewSessionID()));
            Log.d("INFO", "Session now: " + session.getSessionID());
            Log.d("INFO", "Session now(SP): " + rwSetting.getStringSetting(context, "Session"));

            Toast.makeText(context, "Image is being saved", Toast.LENGTH_LONG).show();
            return false;
        }
    };

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (position) {
            case 0:
                ft.replace(R.id.content_frame, fragment1);
                break;

            case 1:
                ft.replace(R.id.content_frame, fragment2);
                break;

        }
        ft.commit();
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        System.gc();
        Log.d("INFO", "Back pressed in FragmentActivity");
        CleanStartUp csu = new CleanStartUp();
        csu.cleanUpOnStart();

        Intent intent = new Intent(this, SelectionActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

}


