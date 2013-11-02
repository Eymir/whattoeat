package com.example.whattoeat;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


public class MainActivity extends Activity {

	private Button optbtn = null;
	private View goimg = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		goimg = super.findViewById(R.id.goimg);
		
		optbtn = (Button) super.findViewById(R.id.optionbtn);
		optbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// add option menu
			}
		});

		goimg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "get img", Toast.LENGTH_SHORT).show();
			}
		});

		
		goimg = super.findViewById(R.id.goimg);
		
		optbtn = (Button) super.findViewById(R.id.optionbtn);
		optbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// add option menu
			}
		});

		goimg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "get img", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, RestauruntActivity.class);
				startActivity(intent); 
				MainActivity.this.finish(); 
			}
		});
		
		ImageView test1 = new ImageView(this);
		ImageView test2 = new ImageView(this);
		ImageView test3 = new ImageView(this);
		ImageView test4 = new ImageView(this);
		test1.setImageResource(R.drawable.test1);
		test2.setImageResource(R.drawable.test2);
		test3.setImageResource(R.drawable.test3);
		test4.setImageResource(R.drawable.test4);
		LinearLayout tbox = (LinearLayout) findViewById(R.id.historybox);
		LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
		tbox.addView(test1,tp);
		tbox.addView(test2,tp);
		tbox.addView(test3,tp);
		tbox.addView(test4,tp);
	}
	
	


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
