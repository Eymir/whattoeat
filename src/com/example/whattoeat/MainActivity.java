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
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;

import android.util.Log;
import android.view.Gravity;
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

	SQLiteDatabase db;
	DBHelper helper = new DBHelper(MainActivity.this);
	
	private Button optbtn = null;
	private View goimg = null;
	private LinearLayout hbox; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		hbox = (LinearLayout) findViewById(R.id.historybox);
		goimg = super.findViewById(R.id.goimg);
		
		optbtn = (Button) super.findViewById(R.id.optionbtn);
		optbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// add option menu
				db = helper.getWritableDatabase();
				db.execSQL("DROP TABLE IF EXISTS history");
				final String INIT_TABLE = "CREATE TABLE history (" +
		                "_ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
						"date VARCHAR, " +
		                "restaurant VARCHAR, " +
		                "menu VARCHAR, " +
		                "photo VARCHAR, " +
		                "rating INTEGER, " +
		                "comment TEXT" +
		                ");"; 
				db.execSQL(INIT_TABLE);
				
				db.close();
			}
		}); 

		goimg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Toast.makeText(getApplicationContext(), "get img", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, PagerActivity.class);
				startActivity(intent); 
				MainActivity.this.finish(); 
			}
		});
		
		
		
		db = helper.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("select * from history order by `date` desc", null);
		int rowsNum = cursor.getCount();
		int item,rating;
		String photo,title,rest;
		Log.d("emo","get rows num"+rowsNum);
		if (rowsNum != 0) {
			cursor.moveToFirst();
			for (int i = 0; i < rowsNum; i++) {
				item = cursor.getInt(0);
				rest = cursor.getString(2);
				title = cursor.getString(3);
				photo = cursor.getString(4);
				rating = cursor.getInt(5);
				addHist(item,rest,title,photo,rating);
				Log.d("emo",title+"    "+rest);
				if (photo != null)
					Log.d("emo","photo "+photo);
				cursor.moveToNext();
			}
		}
		cursor.close();
		
	
		//addHist(R.drawable.test1);
		//addHist(R.drawable.test2);
		//addHist(R.drawable.test3);
		//addHist(R.drawable.test4);
	}
	
	public void addHist(int item, String rest, String title, String photo, int rating)
	{
		RatingBar rbar = new RatingBar(getApplicationContext(),null,android.R.attr.ratingBarStyleSmall);
		RelativeLayout rll = new RelativeLayout(getApplicationContext());
		TextView tv = new TextView(getApplicationContext());
		ImageView nh = new ImageView(getApplicationContext());
		nh.setScaleType(ImageView.ScaleType.FIT_CENTER);
		if (photo == null || photo.equals("")) {
			nh.setImageResource(R.drawable.test1);
		} else {
			File imgFile = new File(Environment.getExternalStorageDirectory(),photo);
			if(imgFile.exists()) {
				Log.d("emo","file exists");
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = false;
				//options.inPreferredConfig = Config.RGB_565;
				options.inSampleSize = 10;
				//options.inDither = true;
			    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(),options);
			    nh.setImageBitmap(myBitmap);
			    //nh.setPadding(1, 0, 1, 0);
			} else {
				
			}
		}
		// query from server (photo, title)
		tv.setText(title);


		final String food = title;
		final String res = rest;
		//nh.setImageResource(item);
		
		rbar.setRating(rating);
		RelativeLayout.LayoutParams imgparam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT-1, RelativeLayout.LayoutParams.MATCH_PARENT-10);
		RelativeLayout.LayoutParams rbparam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		//RelativeLayout.LayoutParams tvparam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams tpbig = new LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.MATCH_PARENT);
		hbox.addView(rll,tpbig);

		rll.setBackgroundResource(R.drawable.border);
		
		rll.addView(tv);
		rll.addView(nh,imgparam);
		tv.setId(1);
		rbparam.addRule(RelativeLayout.BELOW,tv.getId());
		rll.addView(rbar,rbparam);
		
		nh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, ItemActivity.class);
				
				Bundle bundle = new Bundle();
				bundle.putString("food", food);
				bundle.putString("restaurant", res);
				intent.putExtras(bundle);
				
				startActivity(intent);
				MainActivity.this.finish();
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

}
