package edu.ucla.cs.sourcecodes;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
    private View mContentView = null;
    private SessionDataMap sessionData;
    private ArrayList<ListViewItem> items;
    ListView listView;
    String[] values;

    private ArrayList<String> array;

    private String file = "mydata2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textview_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sessionData = new SessionDataMap();

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
                            ArrayAdapter<String> adapter  = new ArrayAdapter<>(getApplicationContext(), R.layout.listview_layout, android.R.id.text1, array);
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

        if (myInternalFile.exists()) {
            try {
                String formArray = "";
                FileInputStream fis = new FileInputStream(myInternalFile);
                DataInputStream in = new DataInputStream(fis);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                while ((strLine = br.readLine()) != null) {
                    formArray = formArray + strLine;
                }
                in.close();
                sessionData = JsonUtil.toSdata(formArray);
            } catch (Exception e) {
                e.printStackTrace();
            }


            // try and get current session from mapping
            if (!sessionData.getCurSessionName().equals("")) {
                for (String word : sessionData.getSession(sessionData.getCurSessionName()).getWordList()) {
                    array.add(word);
                }
                getSupportActionBar().setTitle(sessionData.getCurSessionName());
            }
            else
            {
                //Assume that we didn't load anything from SessionData
                //Load this for new file first made
                sessionData.setCurSessionName("Temp");
                sessionData.setSession("Temp",new SessionData());
            }

            //set adapter for loaded words
            ArrayAdapter<String> adapter  = new ArrayAdapter<>(getApplicationContext(), R.layout.listview_layout, android.R.id.text1, array);
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


        //add action buttom for drawer on this
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);

        //create a new toggle to switch focus if the menu is slide open or closed
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        //listView.setItemsCanFocus(true);

        toggle.syncState();
        addDrawerItems();
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

        saveSession();

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
        //fill array with session name stored using session
        items = new ArrayList<>();
        items.add(new ListViewItem("Add Session", DrawerArrayAdapter.TYPE_ADD));

        ArrayList<String> fileNames = sessionData.getsNames();

        for (int i = 0; i < sessionData.getLength(); i++)
        {
            items.add(new ListViewItem(fileNames.get(i),DrawerArrayAdapter.TYPE_OBJECT));
        }

        //Update drawer here with sessions found
        mDrawerList = (ListView)findViewById(R.id.navList);
        final DrawerArrayAdapter drawerArrayAdapter = new DrawerArrayAdapter(this, R.id.text, items);

        mDrawerList.setAdapter(drawerArrayAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    //save current array and stuff
                    saveSession();

                    //change current session name to the one clicked
                    ListViewItem curListView = (ListViewItem) drawerArrayAdapter.getItem(position);

                    sessionData.setCurSessionName(curListView.getText());
                    getSupportActionBar().setTitle(curListView.getText());

                    SessionData curData = sessionData.getSession(curListView.getText());

                    //switch array to new one
                    clearArray();
                    for (String word : curData.getWordList()) {
                        array.add(word);
                    }
                    ((ArrayAdapter<String>) listView.getAdapter()).notifyDataSetChanged();

                    //close the drawer
                    DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
                    mDrawerLayout.closeDrawers();
                }
            }
        });
    }

    public void addNewSession(String sessionName)
    {
        sessionData.setSession(sessionName, new SessionData());
        DrawerArrayAdapter drawerArrayAdapter = (DrawerArrayAdapter)mDrawerList.getAdapter();
        drawerArrayAdapter.add(new ListViewItem(sessionName, DrawerArrayAdapter.TYPE_OBJECT));
        drawerArrayAdapter.notifyDataSetChanged();

    }

    public void saveSession()
    {
        SessionData temp = new SessionData();
        temp.setSessionName(sessionData.getCurSessionName());
        temp.setWordList(array);
        sessionData.setSession(temp.getSessionName(),temp);

    }

    public void clearArray()
    {
        //clear the current array and reflect it in the adapter
        array.clear();
        ((ArrayAdapter<String>) listView.getAdapter()).notifyDataSetChanged();
    }

    public void changeCurSessionName(String t)
    {
        //change current session
        sessionData.setCurSessionName(t);
        //update to reflect title
        getSupportActionBar().setTitle(t);

    }

    public String getCurName()
    {
        return sessionData.getCurSessionName();
    }

    public void removeSession(String sName)
    {
        sessionData.removeSession(sName);
    }

    public boolean sessionExist(String name)
    {
        ArrayList<String> names = sessionData.getsNames();
        return names.contains(name);
    }

}
