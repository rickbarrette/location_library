/**
 * DirectionsListFragment.java
 * @date Nov 25, 2011
 * @author ricky barrette
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

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.TwentyCodes.android.overlays.DirectionsOverlay;
import com.google.android.maps.GeoPoint;
import android.app.ListFragment;

import java.util.ArrayList;

/**
 * This fragment will be used to display directions to the user. When a specific
 * direction is clicked, the corrispoding geopoint is returned via listener
 * 
 * @author ricky barrette
 */
public class DirectionsListFragment extends ListFragment {

	/**
	 * A simple interfrace for a directions list fragment
	 * 
	 * @author ricky barrette
	 */
	public interface OnDirectionSelectedListener {

		/**
		 * Called when the user selects a direction from a directions list
		 * 
		 * @param point
		 * @author ricky barrette
		 */
		public void onDirectionSelected(GeoPoint point);

	}

	private OnDirectionSelectedListener mListener;
	private ArrayList<GeoPoint> mPoints;

	/**
	 * Creates a new Directions List Fragment
	 * 
	 * @author ricky barrette
	 */
	public DirectionsListFragment() {
		super();
	}

	/**
	 * Creates a new Directions List Fragment
	 * 
	 * @param listener
	 * @author ricky barrette
	 */
	public DirectionsListFragment(final OnDirectionSelectedListener listener) {
		this();
		mListener = listener;
	}

	/**
	 * Deletes all content in the listview
	 * 
	 * @author ricky barrette
	 */
	public void clear() {
		setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, new ArrayList<String>()));
	}

	/**
	 * Called when a list item is clicked. Checks to see if the list item is a
	 * direction, if to it reports the selected direction's geopoint to the
	 * listener (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView,
	 *      android.view.View, int, long)
	 */
	@Override
	public void onListItemClick(final ListView l, final View w, final int position, final long id) {
		if (position < mPoints.size())
			if (mListener != null)
				mListener.onDirectionSelected(mPoints.get(position));
	}

	/**
	 * (non-Javadoc)
	 * 
	 */
	@Override
	public void onStart() {
		setListShown(true);
		super.onStart();
	}

	/**
	 * Displays the directions from the provided DirectionsOverlay object
	 * 
	 * @param directions
	 * @author ricky barrette
	 */
	public void setDirections(final DirectionsOverlay directions) {
		mPoints = directions.getPoints();
		setListAdapter(new DirectionsAdapter(getActivity(), directions));
	}

	/**
	 * Sets the text to be displayed while the list is .gitkeep
	 * 
	 * @param text
	 * @author ricky barrette
	 */
	public void SetEmptyText(final String text) {
		setEmptyText(text);
	}
}
