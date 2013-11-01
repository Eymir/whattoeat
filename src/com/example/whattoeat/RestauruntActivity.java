package com.example.whattoeat;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ImageView;

public class RestauruntActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_restaurunt);
	}
	
	
	
	//ImageView myImageView = (ImageView)findViewById(R.id.ImageView01);
	
    //myImageView.setImageResource(R.drawable.);
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.restaurunt, menu);
		return true;
	}

}
