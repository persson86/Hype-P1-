package com.humoule.customcal.adapter;

import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.humoule.customcal.R;

/**
 * Customize the weekday gridview
 */
public class WeekdayArrayAdapter extends ArrayAdapter<String>
{
	public static int textColor = Color.parseColor("#f7901f");

	public WeekdayArrayAdapter(Context context, int textViewResourceId, List<String> objects) {
		super(context, textViewResourceId, objects);
	}

	// To prevent cell highlighted when clicked
	@Override
	public boolean areAllItemsEnabled()
	{
		return false;
	}

	@Override
	public boolean isEnabled(int position)
	{
		return false;
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// To customize text size and color
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		TextView textView = (TextView) inflater.inflate(R.layout.weekday_textview, null);

		// Set content
		String item = getItem(position);
		textView.setText(item);

		textView.setTextColor(textColor);
		textView.setGravity(Gravity.CENTER);
		return textView;
	}

}
