/**
 * FileName: SpinnerAdapater
 * Author: FlyZebra
 * Email:flycnzebra@gmail.com
 * Date: 2023/9/12 20:51
 * Description:
 */
package com.flyzebra.mdrvset.adapder;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.flyzebra.mdvrset.R;

public class SpinnerAdapater extends ArrayAdapter {
    private Context mContext;
    private String[] mStringArray;

    public SpinnerAdapater(Context context, String[] stringArray) {
        super(context, android.R.layout.simple_spinner_item, stringArray);
        mContext = context;
        mStringArray = stringArray;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
        tv.setBackgroundResource(R.drawable.text_button);
        tv.setText(mStringArray[position]);
        tv.setTextColor(Color.BLACK);
        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
        }

        TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
        tv.setBackgroundResource(R.drawable.text_button);
        tv.setText(mStringArray[position]);
        tv.setTextColor(Color.BLACK);
        return convertView;
    }
}