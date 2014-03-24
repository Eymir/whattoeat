package com.example.whattoeat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

	SQLiteDatabase db;
	DBHelper helper = new DBHelper(MainActivity.this);
	
	private TextView optbtn = null;
	private View goimg = null;
	private LinearLayout hbox; 
	
	
	 private Spinner spinnerTired;
	 private Spinner spinnerPrice;
	 private String[] tiredList = {"一天","三天","一個禮拜","一個月","每餐都一樣也沒問題!!"};
	 private String[] priceList = {"100","300","500","1000","土豪的節奏"};
	 private ArrayAdapter<String> tiredListAdapter;
	 private ArrayAdapter<String> priceListAdapter;
	 private Context mContext;
	 private ConnectivityManager CM;
	 private LocationManager LocationStatus;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);	//connection info
		LocationStatus = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));	//location info
		
		setFilterLayout();
		
	
		hbox = (LinearLayout) findViewById(R.id.historybox);
		goimg = super.findViewById(R.id.goimg);
		
		optbtn = (TextView) super.findViewById(R.id.optionbtn);
		

	
		optbtn.setOnTouchListener(btTouchListener);
		optbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// add option menu
				
				
				
		       // mContext = this.getApplicationContext();

				
				LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
				View filterView = inflater.inflate(R.layout.filter_dialog,null);
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				
				builder.setTitle("請輸入偏好");
				//builder.setMessage("請輸入使用者名稱：");
				builder.setView(filterView);
				AlertDialog dialog = builder.create();
				dialog.show();


				spinnerTired = (Spinner)filterView.findViewById(R.id.tireTimespinner);
				spinnerPrice = (Spinner)filterView.findViewById(R.id.acceptiblePricespinner);
				
				tiredListAdapter = new ArrayAdapter<String>(MainActivity.this, R.drawable.my_spinner_item, tiredList);
		        spinnerTired.setAdapter(tiredListAdapter);
		        
		        priceListAdapter = new ArrayAdapter<String>(MainActivity.this, R.drawable.my_spinner_item, priceList);
		        spinnerPrice.setAdapter(priceListAdapter);

		        spinnerTired.setOnItemSelectedListener(new OnItemSelectedListener(){
		            @Override
		            public void onItemSelected(AdapterView<?> arg0, View arg1,int position, long arg3) {
		               //Toast.makeText(mContext, "你選的是"+tiredList[position], Toast.LENGTH_SHORT).show();

		            }
		            @Override
		            public void onNothingSelected(AdapterView<?> arg0) {
		               // TODO Auto-generated method stub
		            }
		        });
				
		        spinnerPrice.setOnItemSelectedListener(new OnItemSelectedListener(){
		            @Override
		            public void onItemSelected(AdapterView<?> arg0, View arg1,int position, long arg3) {
		               //Toast.makeText(mContext, "你選的是"+priceList[position], Toast.LENGTH_SHORT).show();

		            }
		            @Override
		            public void onNothingSelected(AdapterView<?> arg0) {
		               // TODO Auto-generated method stub
		            }
		        });
				
				
				/*
				new AlertDialog.Builder(MainActivity.this)
				.setTitle("hello")
				.setPositiveButton("確定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// continue with delete
							}
						}).show();
				
				*/
				
				
				
				
				/*
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
				
				db.close();*/
			}
		}); 

		goimg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
				if(CM.getActiveNetworkInfo() == null)
				{
					//alertWithSetting("請開啟網路服務");
					alertWithSetting("請開啟網路服務",new Intent(Settings.ACTION_WIFI_SETTINGS));
				}
				else if (!(LocationStatus.isProviderEnabled(LocationManager.GPS_PROVIDER)
						|| LocationStatus.isProviderEnabled(LocationManager.NETWORK_PROVIDER))) {
					alertWithSetting("請開啟定位服務",new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
				} 
				else
				{				
					// TODO Auto-generated method stub
					//Toast.makeText(getApplicationContext(), "get img", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent();
					intent.setClass(MainActivity.this, PagerActivity.class);
					startActivity(intent); 
					MainActivity.this.finish(); 
				}
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
	
	private void setFilterLayout()
	{
		
		
		
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
	
	
	
	private OnTouchListener btTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			TextView tv = (TextView) v;
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				v.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.border_corner));

				tv.setTextColor(Color.WHITE);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				v.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.border_corner2));
				tv.setTextColor(Color.BLACK);
				// v.setBackgroundDrawable();
			}
			// TODO Auto-generated method stub
			return false;
		}

	};
	
	private void alertWithSetting(String message,final Intent i)
	{
		new AlertDialog.Builder(MainActivity.this)
		.setTitle(message)
		.setPositiveButton("確定",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						startActivity(i); // 開啟設定頁面
						// continue with delete
					}
				}).show();
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
