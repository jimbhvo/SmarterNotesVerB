package edu.ucla.cs.sourcecodes;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.androidbelieve.drawerwithswipetabs.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jimmy on 5/20/2016.
 */
public class DrawerArrayAdapter extends ArrayAdapter {

    public static final int TYPE_ADD = 0;
    public static final int TYPE_OBJECT = 1;
    final private AlertDialog.Builder alert;


    private ArrayList<ListViewItem> objects;

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return objects.get(position).getType();
    }

    public DrawerArrayAdapter(Context context, int resource, ArrayList<ListViewItem> objects) {
        super(context, resource, objects);
        this.objects = objects;
        alert = new AlertDialog.Builder(context);

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        ListViewItem listViewItem = objects.get(position);
        int listViewItemType = getItemViewType(position);

        final TextView textView;
        if (convertView == null) {

            if (listViewItemType == TYPE_ADD) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_drawer_add, null);
                textView = (TextView) convertView.findViewById(R.id.add_item_string);

                Button button = (Button)convertView.findViewById(R.id.drawer_add_btn);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Add new session
                        //Create a new alert dialogue
                        //save current array to current session
                        //add to array? (this.add(new Listview etc)
                        //update sessiondatamap (for save purposes)
                        //switch current array to new session + change session name

                        //TODO: If existing name exist, then just switch to it????
                        final NoteActivity MA = (NoteActivity) getContext();

                        final EditText edittext = new EditText(MA.getApplicationContext());
                        edittext.setTextColor(Color.BLACK);

                        alert.setMessage("Enter A Name");
                        alert.setTitle("Add a new session");

                        alert.setView(edittext);

                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Save new name to session
                                String textValue = edittext.getText().toString();
                                if (!MA.sessionExist(textValue))
                                {
                                    MA.saveSession();
                                    MA.addNewSession(textValue);
                                    MA.clearArray();
                                    MA.changeCurSessionName(textValue);
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

            } else {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_drawer_layout, null);
                textView = (TextView) convertView.findViewById(R.id.list_item_string);

                Button button = (Button)convertView.findViewById(R.id.delete_btn);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //If Not Current session name
                        //Remove session from sesion list
                        //update navlist
                        ListViewItem listItem = objects.get(position);
                        textView.setText("ababab");
                        final NoteActivity MA = (NoteActivity) getContext();
                        if (MA.getCurName() != listItem.getText()) {
                            MA.removeSession(listItem.getText());
                            objects.remove(listItem);
                            remove(listItem);
                            notifyDataSetChanged();

                            //remove from sessiondatamap?
                        }
                    }
                });
            }

            viewHolder = new ViewHolder(textView);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.getText().setText(listViewItem.getText());

        return convertView;
    }

}
