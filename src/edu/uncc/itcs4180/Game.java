/*
 * James Keller
 * ITCS 4180 - 091
 * 2/25/14
 * HW3 - Treasure Game
 */

package edu.uncc.itcs4180;

import java.util.ArrayList;
import java.util.Collections;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

public class Game extends Activity {
	
	final String LOG_TAG = "demo";
	final static String TIME_KEY = "TIME";  //Constant for intent
	public int[] iconIds = new int[] {R.drawable.diamond, R.drawable.garnet, R.drawable.gem, R.drawable.pearl, R.drawable.ruby, R.drawable.sapphire, R.drawable.swarovski, R.drawable.toppaz};
	public ArrayList<Game.Card> Cards = new ArrayList<Game.Card>();
	public ArrayList<Integer> targetImage = new ArrayList<Integer>();  //Holds the resource id's for each icon
	ImageView focusImage;
	private long startTime;  //timing field
	private long endTime;  //timing field
	private long duration;  //timing field
	private int numberClicks = 0;  //Counter for number of block touches
	
	private final Handler handler = new Handler();	
	private boolean touchEnabled = true;
	
	private int indexCount = 7;
	private static int count;
	private int size = 4;
	public Card c1, c2;
	
	//Card class
	public class Card implements View.OnClickListener{
		public ImageView iv;
		public int imageId;
		public boolean isDone;
		public boolean isCovered;
		//Card object constructor
		public Card(Context cx, int imageId, int h, int w){
			this.imageId = imageId;
			this.iv = new ImageView(cx);			
			this.iv.setLayoutParams(new TableRow.LayoutParams(h, w));
			this.iv.setOnClickListener(this);			
			this.isDone = false;
			cover();
		}

		public void cover(){
			this.iv.setImageResource(R.drawable.cover);
			this.isCovered = true;
			touchEnabled = true;
		}

		public void uncover(){
			this.iv.setImageResource(this.imageId);
			this.isCovered = false;
		}
		//Method used to uncover all the blocks when the uncover button is used
		public void uncoverAll(){
			this.iv.setImageResource(this.imageId);
			touchEnabled = false;
		
		}
		public void coverAll(){
			this.iv.setImageResource(R.drawable.cover);
		}

		public boolean match(int i, Card c){
			return ((targetImage.get(i) == c.imageId) && (this.imageId == c.imageId));
		}

		@Override
		public void onClick(View v) {			
			if(touchEnabled){
				if(isCovered){				
					if(c1 == null){ //first clicked
						c1 = this;
						uncover();
							//Used to start the system time when the first block is uncovered
							if(numberClicks < 1) {
								startTime = System.currentTimeMillis();
								Log.d(LOG_TAG, startTime + "");
								numberClicks++;
							}
					}else{ //second clicked				
						if(isCovered){
							if(match(indexCount, c1)){
								uncover();
								isDone = true;
								c1.isDone = true;
								c1 = null;
								//Decrements the index count so the next target jewel can be cycled through the ImageView object 
								//for the player
								if(indexCount != 0) {
									indexCount--;
									focusImage.setImageResource(targetImage.get(indexCount));
								}
								count = count - 2;
								//If the game is finished based on the count field, the time elapsed is calculated and passed to the result
								//activity
								if(count <=0){
									//Log.d(LOG_TAG, "Finished");
									endTime = System.currentTimeMillis();
									duration = (endTime - startTime);
									//Log.d(LOG_TAG, duration + "");
									Intent intent = new Intent(Game.this, Result.class);
									intent.putExtra(TIME_KEY, (long)duration);
									startActivity(intent);
								}
							} else{
								touchEnabled = false;
								uncover();
								c2 = this;
								handler.postDelayed(new Runnable() {
									public void run() {
										c1.cover();
										c2.cover();
										c1 = null;
										touchEnabled = true;
									}
								}, 1000);
							}
						}
					}
				}				
			}
			
			}
	}
	
	//New Game Setup
	public void newGameSetup(){		
		count = size*size;
		numberClicks = 0;
		indexCount = 7;

		Cards.clear();		
		for(int i: iconIds){
			Cards.add(new Card(this, i, getResources().getDimensionPixelSize(R.dimen.imageHeight), getResources().getDimensionPixelSize(R.dimen.imageWidth)));
			Cards.add(new Card(this, i, getResources().getDimensionPixelSize(R.dimen.imageHeight), getResources().getDimensionPixelSize(R.dimen.imageWidth)));
		}
		Collections.shuffle(Cards);
		//Resets the array used to randomly select the jewels
		targetImage.clear();
		//Loads the resource id for the jewels into the ArrayList
		for(int id : iconIds) {
			targetImage.add(id);
		}
		//Shuffles the ArrayList to achieve randomization
		Collections.shuffle(targetImage);
		//Sets the image of the jewel to find based on the last index of the ArrayList
		focusImage = (ImageView) findViewById(R.id.imageView1);
		focusImage.setImageResource(targetImage.get(indexCount));
		//Creates the board and pieces
		TableLayout tbl = (TableLayout) findViewById(R.id.tableLayout1);
		tbl.removeAllViews();
		for(int i=0; i< size; i++){
			TableRow tr = new TableRow(this);
			tr.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			for(int j=0; j<size; j++){
				tr.addView(Cards.get(i*size + j).iv);
			}
			tbl.addView(tr);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		//Receives a boolean flag from the result activity to finish the game activity if the exit button was pressed
		if(getIntent().getBooleanExtra(Result.EXIT_KEY, false)) {
			finish();
		}
		
		//Creation of an alert dialog to be used during exception handling
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("An Error Has Occurred!")
        .setMessage("The aplication will now close.")
        .setCancelable(false)
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
        
        //Alert dialog instantiation
        final AlertDialog simpleAlert = builder.create();
        
		try {
			//Creates a new game and initializes
			newGameSetup();
		} catch (Exception e) {
			simpleAlert.show();
		}
		
		//Reinitializes the game based on a button click
		Button b = (Button) findViewById(R.id.button1);
		b.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				newGameSetup();
			}
		});
		//Uncovers all the covers pieces with a delay
		Button bb = (Button) findViewById(R.id.button2);
		bb.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			for(int i=0 ; i < Cards.size();i++){
				Cards.get(i).uncoverAll();
			}
				handler.postDelayed(new Runnable() {
					public void run() {
						for(int i=0 ; i<Cards.size();i++){	
								if(Cards.get(i).isCovered == true){
									Cards.get(i).coverAll();
								}
						}
						touchEnabled = true;
					}
				}, 1000);
			}
		});	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}
	//Allows a new game to be initialized if the player tries again from the result activity
	@Override
	protected void onResume() {
	    super.onResume();
	    newGameSetup();
	}
}

