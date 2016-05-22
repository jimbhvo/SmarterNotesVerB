package edu.ucla.cs.sourcecodes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.androidbelieve.drawerwithswipetabs.R;

import java.util.ArrayList;

/**
 * Created by Jimmy on 5/20/2016.
 */
public class DrawerArrayAdapter extends ArrayAdapter {

    public static final int TYPE_ADD = 0;
    public static final int TYPE_OBJECT = 1;


    private ListViewItem[] objects;

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return objects[position].getType();
    }

    public DrawerArrayAdapter(Context context, int resource, ListViewItem[] objects) {
        super(context, resource, objects);
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        ListViewItem listViewItem = objects[position];
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
                        //do stuff?
                        textView.setText("obobobob");
                    }
                });

            } else {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_drawer_layout, null);
                textView = (TextView) convertView.findViewById(R.id.list_item_string);

                Button button = (Button)convertView.findViewById(R.id.delete_btn);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //do stuff?
                        textView.setText("ababab");
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
