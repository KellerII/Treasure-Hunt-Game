/*
 * James Keller
 * ITCS 4180 - 091
 * 2/25/14
 * HW3 - Treasure Game
 */

package edu.uncc.itcs4180;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Result extends Activity {
	private double time;
	ImageView resultsImage;
	TextView tv;
	TextView tv2;
	final static String EXIT_KEY = "EXIT";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		//Objects created to change the text and image in the activity based on the players time
		resultsImage = (ImageView) findViewById(R.id.imageView1);
		tv = (TextView) findViewById(R.id.textView1);
		tv2 = (TextView) findViewById(R.id.textView2);
		//Retrieves the intent passed from the game activity
		if(getIntent().getExtras() != null) {
			time = getIntent().getExtras().getLong(Game.TIME_KEY);
			time /= 1000.00;
		}
		//Shows the players the elapsed time
		tv2.setText("Time Elapsed: " + time + " seconds");
		//Based on the player's time, this conditional either keeps the existing settings based on the xml or dynamically changes them
		if(time < 50.00) {
			
		} else {
			tv.setText("Sorry! Try Again...");
			resultsImage.setImageResource(R.drawable.lose);
		}
	
		Button button1 = (Button) findViewById(R.id.button1);
		button1.setOnClickListener(new View.OnClickListener() {
			//Finishes the result activity
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		Button button2 = (Button) findViewById(R.id.button2);
		button2.setOnClickListener(new View.OnClickListener() {
			//Creates an intent that passes a boolean value to the game activity and also closes all open activities except the root
			//while returning to the root
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Result.this, Game.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra(EXIT_KEY, true);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.result, menu);
		return true;
	}

}
