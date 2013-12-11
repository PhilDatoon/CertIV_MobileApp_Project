package com.phildatoon.at5;

import com.phildatoon.compass.CompassActivity;
import com.phildatoon.converter.CurrencyConverterActivity;
import com.phildatoon.notepad.NotepadActivity;
import com.phildatoon.stopwatch.StopwatchActivity;
import com.phildatoon.weather.WeatherActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * The home screen or main activity serves as a starting point
 * wherein user has to select one of the five apps/activities
 * by sliding the screen to the left or to the right, including
 * a button component to open selected activity.
 * 
 * @author	Datoon, Philip Bryan B. (131311399)
 * @since	18 November 2013
 * 
 * Sliding ViewPager source code reference:
 * http://developer.android.com/training/animation/screen-slide.html
 */

public class MainActivity extends FragmentActivity {

    private static final int NUM_PAGES = 6;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_main);

    	// instantiates ViewPager and PagerAdapter
    	mPager = (ViewPager) findViewById(R.id.pager);
    	mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
    	mPager.setAdapter(mPagerAdapter);
    	
    	mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
            public void onPageSelected(int position) {
				invalidateOptionsMenu();
            }
        });
    }
    
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);

        // defines "Open" button component
        MenuItem openBtn = menu.findItem(R.id.btn_open);
        
        /* Component will be visible if and only if page number is
         * greater than zero. Page 0 is an initial screen indicating
         * user has to swipe screen to the right to select an activity.
         */
        openBtn.setVisible(mPager.getCurrentItem() > 0);

        return true;
    }
	
	@Override
	// sets action to start an activity whenever user pressed "Open" menu item
    public boolean onOptionsItemSelected(MenuItem item) {
    	
		if (mPager.getCurrentItem() != 0) {
			// gets and starts activity by calling getIntent() method
			// to instantiate intent depending on page number
			startActivity(getIntent(mPager.getCurrentItem()));
			
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
    }

	// sets action when user clicks or taps on an image
	public void openActivity(View v) {
		try {
			// gets and starts activity by calling getIntent() method
			// to instantiate intent depending on page number
	    	startActivity(getIntent(mPager.getCurrentItem()));
		} catch (NullPointerException e) {
			// catches the exception if user attempts to press click or tap on image on Page 0
		}
	}
	
	public Intent getIntent(int pageNum) {
		Intent intent = null;
		
		switch (pageNum) {
		case 1: // starts CompassActivity activity (page 1) if selected
        	intent = new Intent(this, CompassActivity.class);
        	break;
            
        case 2: // starts Stopwatch activity (page 2) if selected
        	intent = new Intent(this, StopwatchActivity.class);
        	break;
            
        case 3: // starts Currency Converter activity (page 3) if selected
        	intent = new Intent(this, CurrencyConverterActivity.class);
        	break;
            
        case 4: // starts Weather activity (page 4) if selected
        	intent = new Intent(this, WeatherActivity.class);
        	break;
            
        case 5: // starts Notepad activity (page 5) if selected
        	intent = new Intent(this, NotepadActivity.class);
        	break;
		}
		
		return intent;
	}
	
	@Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, program will proceed to Page 1.
            mPager.setCurrentItem(0);
        }
    }
	
	// represents five MainActivityPageFragment objects in sequence
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

		@Override
        public Fragment getItem(int position) {
            return MainActivityPageFragment.create(position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
