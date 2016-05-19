package edu.ucla.cs.sourcecodes;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.androidbelieve.drawerwithswipetabs.R;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class NoteActivity extends AppCompatActivity {
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private View mContentView = null;
    private SessionData sessionData;
    ListView listView;
    String[] values;

    ArrayList<String> array;

    private String file = "mydata";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textview_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        array = new ArrayList<>();
        mContentView = this.findViewById(android.R.id.content).getRootView();

        listView = (ListView) mContentView.findViewById(R.id.list);
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText edittext = new EditText(getApplicationContext());
                edittext.setTextColor(Color.BLACK);
                alert.setMessage("Enter Your Message");
                alert.setTitle("Add Word To List");

                alert.setView(edittext);

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //

                        String textValue = edittext.getText().toString();

                        array.add(textValue);
                        Log.d("MYTAG", textValue);
                        if (array != null) {
                            values = array.toArray(new String[array.size()]);
                            ArrayAdapter<String> adapter  = new ArrayAdapter<>(getApplicationContext(), R.layout.listview_layout, android.R.id.text1, values);
                            listView.setAdapter(adapter);


                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        int position, long id) {
                                    // ListView Clicked item value
                                    String word = (String) listView.getItemAtPosition(position);
                                    Log.d("MYTAGS", word);

                                    new AsyncTaskParseXML(word, getBaseContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                                }
                            });
                        }
                    }
                });

                alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // what ever you want to do with No option.
                        dialog.cancel();
                    }
                });
                alert.show();
            }
        });

        // Load data from json?
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File directory = contextWrapper.getDir(file, Context.MODE_PRIVATE);
        File myInternalFile = new File(directory , file);
        sessionData = null;

        if (myInternalFile.exists()) {
            try {
                String formArray = "";
                FileInputStream fis = new FileInputStream(myInternalFile);
                DataInputStream in = new DataInputStream(fis);
                BufferedReader br =
                        new BufferedReader(new InputStreamReader(in));
                String strLine;
                while ((strLine = br.readLine()) != null) {
                    formArray = formArray + strLine;
                }
                in.close();

                sessionData = JsonUtil.toSdata(formArray);
                sessionData.setSessionName("Temp");

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Set array data
            for (String word : sessionData.getWordList()) {
                array.add(word);
            }

            //set adapter for loaded words
            values = array.toArray(new String[array.size()]);
            ArrayAdapter<String> adapter  = new ArrayAdapter<>(getApplicationContext(), R.layout.listview_layout, android.R.id.text1, values);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    String word = (String) listView.getItemAtPosition(position);
                    Log.d("MYTAGS", word);

                    new AsyncTaskParseXML(word, getBaseContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }

            });
        }

        //Update drawer here with sessions found
        mDrawerList = (ListView)findViewById(R.id.navList);

        //add action buttom for drawer on this
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);

        //create a new toggle to switch focus if the menu is slide open or closed
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                listView.clearFocus();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                listView.requestFocus();
            }
        };
        drawer.setDrawerListener(toggle);
        //listView.setItemsCanFocus(true);

        toggle.syncState();
        addDrawerItems();

        //on item click, do something
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(NoteActivity.this, "Time for an upgrade!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        /*
         * Use OnPause because it's guaranteed to always happen
         * Basically, this save function always happen
         * Possibility of memory leak?? Need to check for validity.
         */
        sessionData = new SessionData();
        sessionData.setSessionName("Temp");
        sessionData.setWordList(array);

        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File directory = contextWrapper.getDir(file, Context.MODE_PRIVATE);
        File myInternalFile = new File(directory , file);

        String saveData = JsonUtil.toJson(sessionData);
        if (saveData != null)
        try{
            FileOutputStream fout = new FileOutputStream(myInternalFile);
            fout.write(saveData.getBytes());
            fout.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addDrawerItems() {
        String[] osArray = { "Android", "iOS", "Windows", "OS X", "Linux" };
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);
    }

}
