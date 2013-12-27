package com.example.whattoeat;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class MainActivity extends Activity {

	private Button optbtn = null;
	private View goimg = null;
	private LinearLayout hbox;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		goimg = super.findViewById(R.id.goimg);
		hbox = (LinearLayout) super.findViewById(R.id.historybox);
		
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
		
		ImageView v1 = (ImageView) findViewById(R.id.iv1);
		v1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, ItemActivity.class);
				startActivity(intent);
				MainActivity.this.finish();
			}
		});
		/*ImageView test1 = new ImageView(this);
		ImageView test2 = new ImageView(this);
		ImageView test3 = new ImageView(this);
		ImageView test4 = new ImageView(this);
		test1.setImageResource(R.drawable.test1);
		test2.setImageResource(R.drawable.test2);
		test3.setImageResource(R.drawable.test3);
		test4.setImageResource(R.drawable.test4);
		test1.setPadding(2, 0, 2, 0);
		test2.setPadding(2, 0, 2, 0);
		test3.setPadding(2, 0, 2, 0);
		test4.setPadding(2, 0, 2, 0);
		
		LinearLayout tbox = (LinearLayout) findViewById(R.id.historybox);
		LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.MATCH_PARENT);
		tbox.addView(test1,tp);
		tbox.addView(test2,tp);
		tbox.addView(test3,tp);
		tbox.addView(test4,tp);*/
		addHist(R.drawable.test1);
		addHist(R.drawable.test2);
		addHist(R.drawable.test3);
		addHist(R.drawable.test4);
	}
	
	public void addHist(int item)
	{
//		RatingBar rbar = new RatingBar(getApplicationContext(),null,android.R.attr.ratingBarStyleSmall);
//		LinearLayout nll = new LinearLayout(getApplicationContext());
//		//LinearLayout nlrb = new LinearLayout(getApplicationContext());
//		RelativeLayout rll = new RelativeLayout(getApplicationContext());
//		ImageView nh = new ImageView(getApplicationContext());
//		nll.setOrientation(LinearLayout.VERTICAL);
//		nh.setImageResource(item);
//		nh.setPadding(1, 0, 1, 0);
//		rbar.setRating(5);
//		//RelativeLayout.LayoutParams imgparam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT-10);
//		RelativeLayout.LayoutParams rbparam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//		rbparam.addRule(RelativeLayout.ALIGN_BOTTOM);
//		LinearLayout.LayoutParams tpbig = new LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.MATCH_PARENT);
//		hbox.addView(rll,tpbig);
//		LinearLayout.LayoutParams tpiv = new LinearLayout.LayoutParams(300,40);
//		LinearLayout.LayoutParams tprb = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//		
//		LinearLayout.LayoutParams tpsmall = new LinearLayout.LayoutParams(300, 20);
//		//hbox.setGravity(Gravity.TOP);
//		//nll.setGravity(Gravity.TOP);
//		//hbox.addView(nll, tpbig);
//		
//		//hbox.addView(nlrb, tpsmall);
//		//nll.addView(rbar, tprb);
//		nll.addView(nh, tpiv);
//		//rll.setGravity(Gravity.CENTER);
//		
//		//rbparam.addRule(RelativeLayout.BELOW);
//		rll.addView(nll,rbparam);
//		rll.addView(rbar,rbparam);
		//rll.addView(nh,imgparam);
		
		return;
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
