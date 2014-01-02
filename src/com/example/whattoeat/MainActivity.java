package com.example.whattoeat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

	private static String server_url_load = "http://192.168.1.101/wteRestName.php";
	JSONParser jsonParser = new JSONParser();
	JSONArray histmenu = null;
	SQLiteDatabase db;
	DBHelper helper = new DBHelper(MainActivity.this);
	
	private Button optbtn = null;
	private View goimg = null;
	private LinearLayout hbox;
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
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, PagerActivity.class);
				startActivity(intent); 
				//MainActivity.this.finish(); 
			}
		});
		
		db = helper.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("select * from history", null);
		int rowsNum = cursor.getCount();
		int item,title,rating,rest;
		String photo;

		if (rowsNum != 0) {
			cursor.moveToFirst();
			for (int i = 0; i < rowsNum; i++) {
				item = cursor.getInt(0);
				rest = cursor.getInt(2);
				title = cursor.getInt(3);
				photo = cursor.getString(4);
				rating = cursor.getInt(5);
				addHist(item,rest,title,photo,rating);
				cursor.moveToNext();
			}
		}
		cursor.close();
		
	
		//addHist(R.drawable.test1);
		//addHist(R.drawable.test2);
		//addHist(R.drawable.test3);
		//addHist(R.drawable.test4);
	}
	
	public void addHist(int item, int rest, int title, String photo, int rating)
	{
		RatingBar rbar = new RatingBar(getApplicationContext(),null,android.R.attr.ratingBarStyleSmall);
		RelativeLayout rll = new RelativeLayout(getApplicationContext());
		TextView tv = new TextView(getApplicationContext());
		ImageView nh = new ImageView(getApplicationContext());
		if (photo.equals("")) {
			
		} else {
			File imgFile = new File(photo);
			if(imgFile.exists()) {
			    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
			    nh.setImageBitmap(myBitmap);
			    nh.setPadding(1, 0, 1, 0);
			} else {
				
			}
		}
		// query from server (photo, title)
		LoadFromServer query = new LoadFromServer();
		query.setParams(rest);
		query.setTV(tv);
		query.execute();
		//final int itemid = item;
		final String photolocation = photo;
		final int restaurant = rest;
		//nh.setImageResource(item);
		
		rbar.setRating(rating);
		RelativeLayout.LayoutParams imgparam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT-10);
		RelativeLayout.LayoutParams rbparam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

		LinearLayout.LayoutParams tpbig = new LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.MATCH_PARENT);
		hbox.addView(rll,tpbig);
		
		rbparam.addRule(RelativeLayout.BELOW);
		rll.setBackgroundResource(R.drawable.border);
		rll.addView(nh,imgparam);
		rll.addView(tv);
		rll.addView(rbar,rbparam);
		
		nh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, ItemActivity.class);
				
				Bundle bundle = new Bundle();
				bundle.putString("photo", photolocation);
				bundle.putInt("restaurant", restaurant);
				
				intent.putExtras(bundle);
				
				startActivity(intent);
				//MainActivity.this.finish();
			}
		});
		return;
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	

	class LoadFromServer extends AsyncTask<String, String, String> {
		private int restaurant;
		private String name;
		private TextView target;
		public void setParams(int r) {
			restaurant = r;
		}
		
		public void setTV(TextView tgt) {
			target = tgt;
		}
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("restaurant", Integer.toString(restaurant)));
			JSONObject json = jsonParser.makeHttpRequest(server_url_load,
                    "POST", params);
			
			try {
				 int success = json.getInt("success");
				 if (success == 1) {
					 //histmenu = json.getJSONArray("restaurant");
					 name = json.getString("restaurant");
					 target.setText(name);
				 }
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
	}
}
