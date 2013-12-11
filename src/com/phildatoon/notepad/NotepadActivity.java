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

import com.phildatoon.at5.R;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class NotepadActivity extends ListActivity {
    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;
    private static final int EDIT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
	private ActionBar actionBar;
    private NotesDbAdapter mDbHelper;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepad);
        
        // adds back button on the action bar
        actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        fillData();
        registerForContextMenu(getListView());
    }

	@SuppressWarnings("deprecation")
	protected void fillData() {
        Cursor notesCursor = mDbHelper.fetchAllNotes();
        startManagingCursor(notesCursor);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{ NotesDbAdapter.KEY_TITLE };

        // and an array of the fields we want to bind those fields to
        int[] to = new int[] { R.id.note_title };
        
        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter notes = 
            new SimpleCursorAdapter(this, R.layout.notepad_row, notesCursor, from, to);
        setListAdapter(notes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.notepad, menu);
        
        return true;
    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
        case R.id.action_add_note:
        	// executes action if "Add note" menu item is selected
        	createNote();
            return true;
            
        case android.R.id.home:
        	// finishes the activity once user pressed back button on action bar
    		this.finish();
    		return true;
    		
        default:
            return super.onOptionsItemSelected(item);
		}
	}

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {    	
        super.onCreateContextMenu(menu, v, menuInfo);
    	
        menu.setHeaderTitle("Options");
        menu.add(0, EDIT_ID, 0, R.string.edit_note);
        menu.add(0, DELETE_ID, 0, R.string.action_delete_note);
    }

	@Override
    public boolean onContextItemSelected(MenuItem item) {
    	final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	
        switch(item.getItemId()) {
        	case EDIT_ID:
        		// calls method to edit note
                editNote(info.id);
        		return true;
            case DELETE_ID:
            	AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
    			
                alertDialog.setTitle("Deleting note \""+
                		mDbHelper.fetchNote(info.id)
            			.getString(mDbHelper.fetchNote(info.id)
            			.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)) + "\"");
                alertDialog.setMessage("Are you sure?");
                alertDialog.setIcon(R.drawable.ic_action_warning);
 
                // sets "Yes" Button
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    	// deletes note
                        mDbHelper.deleteNote(info.id);
                        fillData();
                    }
                });
 
                // sets "No" Button
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    	// hides or closes the dialog box
                    	dialog.cancel();
                    }
                });

                // shows dialog box
                alertDialog.show();
            	
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void createNote() {
        Intent i = new Intent(this, NoteEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }
    
    private void editNote(long id) {
    	Intent i = new Intent(this, NoteEdit.class);
        i.putExtra(NotesDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        editNote(id);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }
}