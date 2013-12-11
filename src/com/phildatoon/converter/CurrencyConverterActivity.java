package com.phildatoon.converter;

/**
 * Source code reference
 * https://github.com/mrnayak/CurrencyConverterActivity
 * 
 * Retrieved 25 Nov 2013
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.*;

import com.phildatoon.at5.AlertDialogDisplay;
import com.phildatoon.at5.R;

@SuppressLint("DefaultLocale")
public class CurrencyConverterActivity extends Activity {
	public Button btn_convert;
	public int to;
	public int from;
	public String [] val;
	public String [] symbol;
	public String s;
	public Handler handler;
	private ActionBar actionBar;
	private EditText txt_fromValue;
	private Spinner s1;
	private Spinner s2;
	private TextView result;
	private TextView eqOfOne;
    private ProgressDialog mProgressDialog;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);
        
        // permits certain versions to access network
        if (android.os.Build.VERSION.SDK_INT > 11) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();
        mProgressDialog.setMessage("Loading");
        
		// adds back button on the action bar
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
        
        s1 = (Spinner) findViewById(R.id.spinner1);
        s2 = (Spinner) findViewById(R.id.spinner2);
        
		txt_fromValue = (EditText) findViewById(R.id.fromValue);
		eqOfOne = (TextView) findViewById(R.id.eqOfOne);
		
		eqOfOne.setVisibility(View.INVISIBLE);
		
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.name, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        
        val  = getResources().getStringArray(R.array.value);
        symbol  = getResources().getStringArray(R.array.symbol);
        
        s1.setAdapter(adapter);
        s2.setAdapter(adapter);
        
        s1.setOnItemSelectedListener(new spinOne(1));
        s2.setOnItemSelectedListener(new spinOne(2));
        
        btn_convert = (Button) findViewById(R.id.convertBtn);

        new Utility();
        
        try {
			if (!Utility.isConnected(getApplicationContext())) {
	        	throw (new Exception());
	        } else {				
	        	btn_convert.setOnClickListener(new View.OnClickListener(){
	        		public void onClick(View v) {
	        			result = (TextView) findViewById(R.id.conversionResult);
	        			
	    				try {
	    			    	showProgressDialog("Converting");
	    					s = getJson("http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20(%22"+val[from]+val[to]+"%22)&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=");	                    
	    					
	    					JSONObject jObj;
	    					jObj = new JSONObject(s);
	    					String exResult = jObj.getJSONObject("query").getJSONObject("results").getJSONObject("rate").getString("Rate");
	    					
	    					double fromValue;
	    					double toValue;
	    					
	    					// gets value of "from" editText and convert it to double data type
	    					fromValue = Double.parseDouble(txt_fromValue.getText().toString());
	    					
	    					// multiplies fromValue to the equivalent currency value
	    					toValue = fromValue * Double.parseDouble(exResult);
	    					
	    					DecimalFormat df = new DecimalFormat("#,##0.0000");
	    					
	    					// displays conversion
	    					result.setText(df.format(toValue));
	    					
	    					// displays equivalent currency value of 1
	    					eqOfOne.setVisibility(View.VISIBLE);
	    					eqOfOne.setText("1 " + val[from] + " = " +
	    							df.format(Double.parseDouble(exResult)) + " " + val[to]);
	    					
	    					hideProgressDialog();
	    				} catch (JSONException e) {
	    					// TODO Auto-generated catch block
	    					e.printStackTrace();
	    				} catch (ClientProtocolException e) {
	    					// TODO Auto-generated catch block
	    					e.printStackTrace();
	    				} catch (IOException e) {
	    					// TODO Auto-generated catch block
	    					e.printStackTrace();
	    				} catch (NullPointerException e) {
	    					Toast.makeText(getApplicationContext(), "Please enter a value.",
	    		        			Toast.LENGTH_SHORT).show();
	    				}
	        		}
	            });
	        }
        } catch(Exception e) {
			hideProgressDialog();
        	new AlertDialogDisplay(this, "network");
        }
    }
    
    @SuppressLint("DefaultLocale")
	private void changeButtonState() {    	
    	ImageView fromFlag = (ImageView) findViewById(R.id.fromFlag);
    	ImageView toFlag = (ImageView) findViewById(R.id.toFlag);
    	TextView fromSymbol = (TextView) findViewById(R.id.fromSymbol);
    	TextView toSymbol = (TextView) findViewById(R.id.toSymbol);
    	
    	Button btn_swap = (Button) findViewById(R.id.swapBtn);
    	
    	if (from == to) {
    		// disables button if selected currencies in spinners are of the same value
    		btn_convert.setEnabled(false);
			btn_swap.setEnabled(false);
    		Toast.makeText(getApplicationContext(), "Cannot convert to same currency.",
        			Toast.LENGTH_SHORT).show();
        } else {
        	btn_convert.setEnabled(true);
			btn_swap.setEnabled(true);
        }

    	// changes symbol corresponding to currency being selected
    	fromSymbol.setText(symbol[from]);
    	toSymbol.setText(symbol[to]);
    	
    	// changes flag image corresponding to currency being selected
    	setFlagImage(fromFlag, from);
    	setFlagImage(toFlag, to);
    	
    	hideProgressDialog();
    	
    	clearTexts();
    }
    
    public void setFlagImage(ImageView img, int index) {
    	String countryCode;
    	countryCode = val[index].equals("GBP") ? "uk" : val[index].substring(0, 2).toLowerCase();
    	
    	// changes flag image corresponding to currency being selected
    	img.setImageBitmap(Utility.getBitmapFromWeb(
    			"http://www.libertys.com/dra/drg_" + countryCode + ".gif"));
    }
    
    public String getJson(String url) throws ClientProtocolException, IOException {
		StringBuilder build = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
	    HttpResponse response = client.execute(httpGet);
		HttpEntity entity = response.getEntity();
		InputStream content = entity.getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(content));
		
		String con;
		
		while ((con = reader.readLine()) != null) {
					build.append(con);	
		}
		return build.toString();
	}
    
    private class spinOne implements OnItemSelectedListener {
    	int ide;
    	
    	spinOne(int i) {
    		ide = i;
    	}
    	
    	public void onItemSelected(AdapterView<?> parent, View view,
    			int index, long id) {
    		if (ide == 1)
    			from = index;
    		else if (ide == 2)
    			to = index;
    		
    		changeButtonState();
    	}

    	public void onNothingSelected(AdapterView<?> arg0) {
    		// TODO Auto-generated method stub	
    	}
    }
    
    public void swapCurrencies(View v) {
    	showProgressDialog("Swapping");
    	s1.setSelection(to);
    	s2.setSelection(from);
    	txt_fromValue.setText("1.00");
    }
    
    public void resetForm(View v) {
    	showProgressDialog("Loading");
    	s1.setSelection(0);
    	s2.setSelection(0);
    	txt_fromValue.setText("1.00");
    }
    
    private void clearTexts() {
    	try {
    		// clears result every time spinner state changes
    		result.setText(null);
    		eqOfOne.setText(null);
    		eqOfOne.setVisibility(View.INVISIBLE);
    	} catch (NullPointerException e) {
    	}
    }
    
	private void showProgressDialog(String message) {
      	if (mProgressDialog != null && mProgressDialog.isShowing()) {
      		mProgressDialog.cancel();
      	}
      	
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();
        mProgressDialog.setMessage(message);
	}
	
	private void hideProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.cancel();
		}
	}
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// finishes the activity once user pressed back button on action bar
		this.finish();
		return true;
	}
}