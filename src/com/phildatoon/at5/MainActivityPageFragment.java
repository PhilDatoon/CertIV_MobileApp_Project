package com.phildatoon.at5;

import android.os.Bundle;
import android.support.v4.app.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * @author	Datoon, Philip Bryan B. (131311399)
 * @since	18 November 2013
 * 
 * Source code reference:
 * http://developer.android.com/training/animation/screen-slide.html
 */

public class MainActivityPageFragment extends Fragment {
	public static final String ARG_PAGE = "page";
    private int mPageNumber;
    private ImageView pageImage;
	
    public static MainActivityPageFragment create(int pageNumber) {
    	// constructs a new fragment for the given page number
    	MainActivityPageFragment fragment = new MainActivityPageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(
				R.layout.activity_main_fragment, container, false);
		
		// defines ImageView component
		pageImage = (ImageView) rootView.findViewById(R.id.imageView_home);
		
		// sets image to current fragment being viewed
		switch (mPageNumber) {
    	case 0:
    		pageImage.setImageResource(R.drawable.main_home);
    		break;
    	case 1: 
    		pageImage.setImageResource(R.drawable.main_compass);
    		break;
    	case 2: 
    		pageImage.setImageResource(R.drawable.main_stopwatch);
    		break;
    	case 3: 
    		pageImage.setImageResource(R.drawable.main_converter);
    		break;
    	case 4: 
    		pageImage.setImageResource(R.drawable.main_weather);
    		break;
    	case 5: 
    		pageImage.setImageResource(R.drawable.main_notepad);
    		break;
    }
		
		return rootView;
    }
	
	// returns the page number represented by this fragment object.
	public int getPageNumber() {
        return mPageNumber;
    }
}
