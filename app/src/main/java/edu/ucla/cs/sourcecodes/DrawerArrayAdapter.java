package edu.ucla.cs.sourcecodes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.androidbelieve.drawerwithswipetabs.R;

import java.util.ArrayList;

/**
 * Created by Jimmy on 5/20/2016.
 */
public class DrawerArrayAdapter extends BaseAdapter implements ListAdapter{
    private ArrayList<String> list = new ArrayList<>();
    private Context context;

    public DrawerArrayAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
        //insert special case at slot 0
        list.add(0,"Add a new Session");
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_drawer_layout, null);
        }

        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
        listItemText.setText(list.get(position));
        TextView addItemText = (TextView)view.findViewById(R.id.add_item_string);
        addItemText.setText(list.get(0));

        //Handle buttons and add onClickListeners
        Button deleteBtn = (Button)view.findViewById(R.id.delete_btn);
        Button addBtn = (Button)view.findViewById(R.id.drawer_add_btn);

        addBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                //TODO: Remove session that was deleted IF IT'S NOT CURRENT SESSION
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                //TODO: Remove session that was deleted IF IT'S NOT CURRENT SESSION
            }
        });

        return view;
    }

}
