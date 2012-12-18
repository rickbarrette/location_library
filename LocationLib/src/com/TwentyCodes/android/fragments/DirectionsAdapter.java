/**
 * DirectionsAdapter.java
 * @author ricky barrette
 * @date Sep 22, 2010
 * 
 * Copyright 2012 Richard Barrette 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License
 */
package com.TwentyCodes.android.fragments;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.TwentyCodes.android.location.R;
import com.TwentyCodes.android.overlays.DirectionsOverlay;

/**
 * this is a custom listview adaptor that wills a listview that has 2 textviews
 * in each row.
 * 
 * @author ricky barrette
 */
public class DirectionsAdapter extends BaseAdapter {

	/**
	 * this class will hold the TextViews
	 * 
	 * @author ricky barrette
	 */
	class ViewHolder {
		TextView text;
		TextView text2;
	}

	private final LayoutInflater mInflater;

	private final DirectionsOverlay mDirections;

	/**
	 * Creates a new DirectionsAdapter
	 * 
	 * @author ricky barrette
	 */
	public DirectionsAdapter(final Context context, final DirectionsOverlay directions) {
		mInflater = LayoutInflater.from(context);
		mDirections = directions;
	}

	/**
	 * returns the size of the main list
	 * 
	 * @see android.widget.Adapter#getCount()
	 * @return
	 * @author ricky barrette
	 */
	@Override
	public int getCount() {
		return mDirections.getDirections().size() + 1;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 * @param position
	 * @return
	 * @author ricky barrette
	 */
	@Override
	public Object getItem(final int position) {
		return position;
	}

	/**
	 * returns the current position in the list
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 * @param position
	 * @return
	 * @author ricky barrette
	 */
	@Override
	public long getItemId(final int position) {
		return position;
	}

	/**
	 * inflates the row from xml, and sets the textviews to their intended vales
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 *      android.view.ViewGroup)
	 * @param position
	 * @param convertView
	 * @param parent
	 * @return
	 * @author ricky barrette
	 */
	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_row, null);
			holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(R.id.TextView01);
			holder.text2 = (TextView) convertView.findViewById(R.id.TextView02);

			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		/**
		 * Display the copyrights on the bottom of the directions list
		 */
		if (position == mDirections.getDirections().size()) {
			holder.text.setText(mDirections.getCopyrights());
			holder.text2.setText("");
		} else {
			holder.text.setText(Html.fromHtml(mDirections.getDirections().get(position)));
			holder.text2.setText(mDirections.getDurations().get(position) + " : " + mDirections.getDistances().get(position));
		}
		return convertView;
	}

}