package edu.ucla.cs.sourcecodes;

import android.widget.TextView;

/**
 * Created by Jimmy on 5/21/2016.
 */
public class ViewHolder {
    TextView text;

    public ViewHolder(TextView text) {
        this.text = text;
    }

    public TextView getText() {
        return text;
    }

    public void setText(TextView text) {
        this.text = text;
    }

}
