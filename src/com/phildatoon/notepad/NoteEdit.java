/*
 * Copyright (C) 2008 Google Inc.
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
 * limitations under the License.
 */

package com.phildatoon.notepad;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.phildatoon.at5.R;

public class NoteEdit extends Activity {

    private EditText mTitleText;
    private EditText mBodyText;
    private Long mRowId;
    private NotesDbAdapter mDbHelper;
	private ActionBar actionBar;
	private String title;				// originally saved title
	private String body;				// originally saved body
	private boolean isValid = false;	// sets flag for saving
	private boolean isChanged;			// sets flag for text changes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();

        // adds back button on the action bar
        actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        setContentView(R.layout.notepad_edit);
        setTitle(R.string.edit_note);

        mTitleText = (EditText) findViewById(R.id.title);
        mBodyText = (EditText) findViewById(R.id.body);
        
        mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROWID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID)
									: null;
		}

		populateFields();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.notepad_edit, menu);
        
        return true;
    }
    
    @SuppressWarnings("deprecation")
	private void populateFields() {
        if (mRowId != null) {
            Cursor note = mDbHelper.fetchNote(mRowId);
            startManagingCursor(note);
            mTitleText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
            mBodyText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));
            
            // gets originally saved texts
            this.title = mTitleText.getText().toString();
            this.body = mBodyText.getText().toString();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }
     
    private void saveState() {
    	try {    		
	        String title = mTitleText.getText().toString();
	        String body = mBodyText.getText().toString();

	        if (title.isEmpty() || body.isEmpty()) {
	        	String message;
	        	message = title.isEmpty() && body.isEmpty() ? "title and body" :
	        		title.isEmpty() ? "title" : "body";
	        	
	        	isValid = false;

		        // throws exception on null inputs
	        	throw (new NullPointerException("Enter " + message + "."));
	        	
	        } else {
	        	if (mRowId == null) {
		        	// creates new note then save
		            long id = mDbHelper.createNote(title, body);
		            
		            if (id > 0) {
		                mRowId = id;
		            }
		            
		            Toast.makeText(getApplicationContext(), "Note has been saved.",
		        			Toast.LENGTH_SHORT).show();
		        	isValid = true;
		        } else {
		        	// updates note
		            mDbHelper.updateNote(mRowId, title, body);
		            
		            Toast.makeText(getApplicationContext(), "Note has been updated.",
		        			Toast.LENGTH_SHORT).show();
		        	isValid = true;
		        }
	        }
    	} catch (NullPointerException e) {
    		Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
    		return;
    	}
    }
    
    public boolean getState() {
    	try {
    		if (this.title.isEmpty() || this.body.isEmpty()) {
    			throw (new NullPointerException());
    		}
	        if (!(this.title.equals(mTitleText.getText().toString()))
	        		|| !(this.body.equals(mBodyText.getText().toString()))) {
		    	// checks if there are any changes in texts
	        	// if not equal, then state has changed
	        	isChanged = true;
	        } else {
	        	isChanged = false;
	        }
    	} catch (NullPointerException e) {
    		// if one of the strings is null, it indicates the note is new or to be created
    		isChanged = false;
    	}
        
        return isChanged;
    }
    
    public void saveNote() {
    	if (!isValid) {
			saveState();
		} else {
			// sets back to original state
			isChanged = false;
			this.title = mTitleText.getText().toString();
    		this.body = mBodyText.getText().toString();
    		
			setResult(RESULT_OK);
		}
    }
    
    public void saveNote(View v) {
    	// saves note if "Save" button at the bottom of the screen is pressed
    	saveNote();
    	
    	if (isValid) {
			finish();
		}
    }
    
    public void cancelSaving(View v) {
    	// finishes the activity if "Cancel" button at the bottom of the screen is pressed
		this.finish();
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {			
        case android.R.id.home:
        	
    		if (!getState()) {
            	// finishes the activity once user pressed back button on action bar
    			this.finish();
    	    	return true;
    		} else {
    			AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
    			
                alertDialog.setTitle("Close without saving");
                alertDialog.setMessage("Would you like to save this file?");
                alertDialog.setIcon(R.drawable.ic_action_warning);
 
                // sets "Yes" Button
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    	// saves note
                    	saveNote();
                    	
                    	if (isValid) {
            				finish();
            			}
                    }
                });
 
                // sets "No" Button
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    	// finishes the activity
                    	finish();
                    }
                });
 
                // sets "Cancel" Button
                alertDialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    	// hides or closes the dialog box
                    	dialog.cancel();
                    }
                });
 
                // shows dialog box
                alertDialog.show();
                
                return true;
    		}

		case R.id.action_save_note:
			// saves note once user pressed save button on action bar
			saveNote();
			
			if (isValid) {
				finish();
				return true;
			}
        default:
            return super.onOptionsItemSelected(item);
		}
	}
}
