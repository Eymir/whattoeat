package com.example.whattoeat;


import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.Menu;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings;

@SuppressLint("NewApi")
public class PagerActivity extends Activity implements LocationListener {
	// pager and layout
	private ViewPager myViewPager;
	private MyPagerAdapter myAdapter;
	private LayoutInflater mInflater;
	private List<View> mListViews;
	private View infoLayout = null;
	private View menuLayout = null;
	private View mapLayout = null;
	private View layout3 = null;
	private View myComment = null;
	
	
	// server
	//private String baseUrl ="http://myweb.ncku.edu.tw/~p96024061/testAndroid/index.php?";
	//private String baseUrl = "http://192.168.1.116/wteSuggest.php?";
	private String baseUrl = "http://140.116.86.212/api/query.php?";
	
	
	
	// restaurant info
	private String restName = null; // restaurant name
	private String imageFileURL = null; // restaurant image url
	private String restAddr = null; // restaurant address
	private String restTel = null; // restaurant telephone number
	private String restOpen = null; // restaurant opening time
	private String restClosed = null; // restaurant closed days
	private String restWeb = null; // restaurant web site
	private String restDescription = null; // restaurant price
	private String restMenu = null;
	private Double resLat = null;
	private Double resLng = null;
	private String[] splitMenu;
	
	// flags
	private String lastRes = null;
	private String flag = "0";
	private boolean confirmFlag = true; 
	
	// google map settings
	private GoogleMap map;

	// user info
	private String deviceId;
	private Double longitude;
	private Double latitude;

	private ArrayList<String> mealItem;
	DBHelper helper = new DBHelper(PagerActivity.this);
	SQLiteDatabase db;

	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewpager_layout);
		
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			flag = bundle.getString("flag");

			if (bundle.size() > 1) {
				lastRes = bundle.getString("lastRes");
			}
		}

		db = helper.getWritableDatabase();
		mealItem = new ArrayList<String>();
		/** set pager layout **/
		setPagerItem();

		/**
		 * send deviceId, GPS and last restaurant to server using http GET
		 * request and get restaurant information to initial
		 * **/

		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		deviceId = telephonyManager.getDeviceId();

		initial();

		/**
		 * set restaurant information in activity_restaurant now at HttpTask
		 * **/
		// setRestaurantInfo();

		/**
		 * set map now at HttpTask
		 * **/
		// setMap();

	}

	private void locationServiceInitial() {
		LocationManager lms = (LocationManager) getSystemService(LOCATION_SERVICE); // 取得系統定位服務
		Criteria criteria = new Criteria(); // 資訊提供者選取標準
		String bestProvider = LocationManager.GPS_PROVIDER;
		bestProvider = lms.getBestProvider(criteria, true); // 選擇精準度最高的提供者
		Location location = lms.getLastKnownLocation(bestProvider);
		// Location location =
		// lms.getLastKnownLocation(LocationManager.GPS_PROVIDER); //使用GPS定位座標
		if (location != null) {
			longitude = location.getLongitude(); // 取得經度
			latitude = location.getLatitude(); // 取得緯度

		} else {
			Toast.makeText(this, "無法定位座標", Toast.LENGTH_LONG).show();
		}
	}

	private void initial() {

		// GPS
		LocationManager status = (LocationManager) (this
				.getSystemService(Context.LOCATION_SERVICE));
		if (status.isProviderEnabled(LocationManager.GPS_PROVIDER)
				|| status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			// 如果GPS或網路定位開啟，呼叫locationServiceInitial()更新位置
			locationServiceInitial();
		} else {
			Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();
			startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)); // 開啟設定頁面
		}

		String nowRest = lastRes;
		if (flag.equals("2") && nowRest == null) {
			nowRest = "0";
		}

		if (nowRest != null) {
			try {
				nowRest = URLEncoder.encode(nowRest, "utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		latitude = 22.9;
		longitude = 120.0;

		//String url = baseUrl + "deviceId=" + deviceId + "&lat=" + latitude + "&lng=" + longitude + "&lastRes=" + nowRest + "&flag=" + flag;
		String url = baseUrl + "user_id=" + deviceId + "&latitude=" + latitude + "&longitude=" + longitude + "&index_start=" + 1 + "&index_end=" + 5;
		new HttpTask().execute(url);
	}

	private void setMap() {
		// set map

		int scale = 16;
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();

		if (resLat == null || resLng == null) {
			resLat = 23.46923;
			resLng = 120.957584;
			scale = 5;
		}

		LatLng latlng = new LatLng(resLat, resLng);
		Marker marker = map.addMarker(new MarkerOptions().position(latlng)
				.title(restName).snippet(restAddr));
		marker.showInfoWindow();
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, scale));

	}

	private void setPagerItem() {

		// set pager layout
		mListViews = new ArrayList<View>();
		mInflater = getLayoutInflater();
		infoLayout = mInflater.inflate(R.layout.activity_restaurunt, null);
		menuLayout = mInflater.inflate(R.layout.menu_layout, null);
		mapLayout = mInflater.inflate(R.layout.activity_map, null);
		layout3 = mInflater.inflate(R.layout.layout3, null);
		myComment = mInflater.inflate(R.layout.activity_item, null);
		

		mListViews.add(infoLayout);
		mListViews.add(menuLayout);
		mListViews.add(mapLayout);
		mListViews.add(layout3);
		//mListViews.add(myComment);

		myAdapter = new MyPagerAdapter();
		myViewPager = (ViewPager) findViewById(R.id.viewpagerLayout);
		myViewPager.setAdapter(myAdapter);

		// set pager item
		myViewPager.setCurrentItem(0);
		TextView tv = (TextView) findViewById(R.id.infoTV);
		//tv.setBackground(getResources().getDrawable(R.drawable.border_corner));
		tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_corner));
		tv.setOnClickListener(pagerTVListener);
		tv = (TextView) findViewById(R.id.menuTV);
		tv.setOnClickListener(pagerTVListener);
		tv = (TextView) findViewById(R.id.mapTV);
		tv.setOnClickListener(pagerTVListener);
		tv = (TextView) findViewById(R.id.commentTV);
		tv.setOnClickListener(pagerTVListener);
		
		tv = (TextView) findViewById(R.id.myCommentTV);
		tv.setOnClickListener(pagerTVListener);
		tv.setVisibility(View.GONE);
		
		
		myViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				Log.d("k", "onPageSelected - " + arg0);

				TextView tv = (TextView) findViewById(R.id.infoTV);
				tv.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.border_only_botton));
				tv.setTextColor(Color.BLACK);
				tv = (TextView) findViewById(R.id.menuTV);
				tv.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.border_only_botton));
				tv.setTextColor(Color.BLACK);
				tv = (TextView) findViewById(R.id.mapTV);
				tv.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.border_only_botton));
				tv.setTextColor(Color.BLACK);
				tv = (TextView) findViewById(R.id.commentTV);
				tv.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.border_only_botton));
				tv.setTextColor(Color.BLACK);
				tv = (TextView) findViewById(R.id.myCommentTV);
				tv.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.border_only_botton));
				tv.setTextColor(Color.BLACK);

				
				if (arg0 == 0) {
					tv = (TextView) findViewById(R.id.infoTV);
				}
				if (arg0 == 1) {
					tv = (TextView) findViewById(R.id.menuTV);
				}
				if (arg0 == 2) {
					tv = (TextView) findViewById(R.id.mapTV);
				}
				if (arg0 == 3) {
					tv = (TextView) findViewById(R.id.commentTV);
				}
				if (arg0 == 4) {
					tv = (TextView) findViewById(R.id.myCommentTV);
				}
				tv.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.border_corner));
				tv.setTextColor(Color.WHITE);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				Log.d("k", "onPageScrolled - " + arg0);

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				Log.d("k", "onPageScrollStateChanged - " + arg0);

				/**
				 * Indicates that the pager is in an idle, settled state. The
				 * current page is fully in view and no animation is in
				 * progress.
				 */
				// public static final int SCROLL_STATE_IDLE = 0;
				/**
				 * Indicates that the pager is currently being dragged by the
				 * user.
				 */
				// public static final int SCROLL_STATE_DRAGGING = 1;
				/**
				 * Indicates that the pager is in the process of settling to a
				 * final position.
				 */
				// public static final int SCROLL_STATE_SETTLING = 2;

			}
		});

		// set restart
		TextView restartTV = (TextView) this.findViewById(R.id.restartTV);

		restartTV.setOnTouchListener(btTouchListener);
		restartTV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/*
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				// restName = "XD 貓貓鍋";
				bundle.putString("flag", "1");
				intent.putExtras(bundle);

				intent.setClass(PagerActivity.this, PagerActivity.class);
				startActivity(intent);
				PagerActivity.this.finish();
*/
			}
		});

		// set last one
		TextView backTV = (TextView) this.findViewById(R.id.backTV);
		backTV.setOnTouchListener(btTouchListener);

		backTV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/*
				Intent intent = new Intent();
				Bundle bundle = new Bundle();

				// restName = "貓貓鍋";
				bundle.putString("lastRes", restName);
				bundle.putString("flag", "2");
				intent.putExtras(bundle);
				intent.setClass(PagerActivity.this, PagerActivity.class);
				startActivity(intent);
				PagerActivity.this.finish();
*/
			}
		});

		TextView leaveTV = (TextView) this.findViewById(R.id.leaveTV);
		leaveTV.setOnTouchListener(btTouchListener);
		leaveTV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				TextView tv = (TextView) findViewById(R.id.myCommentTV);
				tv.setVisibility(View.VISIBLE);
				
				mListViews.add(myComment);

				myAdapter = new MyPagerAdapter();
				myViewPager = (ViewPager) findViewById(R.id.viewpagerLayout);
				myViewPager.setAdapter(myAdapter);
				myViewPager.setCurrentItem(4);
				//mListViews.add(myComment);
				/*
				 * Intent intent = new Intent(); Bundle bundle = new Bundle();
				 * //restName = "貓貓鍋"; bundle.putString("lastRes", restName);
				 * bundle.putString("flag", "2"); intent.putExtras(bundle);
				 * intent.setClass(PagerActivity.this, PagerActivity.class);
				 * startActivity(intent); PagerActivity.this.finish();
				 */

			}
		});

	}

	@SuppressLint("NewApi")
	private void setRestaurantInfo() {

		// set restaurant image
		// imageFileURL = "http://pic.pimg.tw/bunnylinn/4bf0b91869bd0.jpg";
		ImageView restIV = (ImageView) infoLayout
				.findViewById(R.id.restaurantImgView);
		UrlImageViewHelper.setUrlDrawable(restIV, imageFileURL);

		// set restaurant name
		// restName = "XM麻辣鍋";
		TextView restNameTV = (TextView) infoLayout.findViewById(R.id.titleTV);
		restNameTV.setText(restName);

		// set restaurant address
		// restAddr = "台南市東區林森路一段167號";
		TextView restAddrTV = (TextView) infoLayout
				.findViewById(R.id.addressTV);
		restAddrTV.setText(restAddr);

		// set restaurant phone #
		// restTel = "06-2083775";
		TextView restPhoneTV = (TextView) infoLayout.findViewById(R.id.phoneTV);
		restPhoneTV.setText(restTel);

		// set restaurant opening time
		// restOpen = "11:00-02:00";
		TextView restOpenTV = (TextView) infoLayout
				.findViewById(R.id.openTimeTV);
		restOpenTV.setText(restOpen);

		// set restaurant closed days
		// restClosed = "無";
		TextView restCloseTV = (TextView) infoLayout
				.findViewById(R.id.closedDaysTV);
		restCloseTV.setText(restClosed);

		// set restaurant web site
		// restWeb = "http://www.xm.512g.com";
		TextView restWebTV = (TextView) infoLayout.findViewById(R.id.webTV);
		restWebTV.setText(restWeb);
		restWebTV.setTextColor(Color.BLUE);
		restWebTV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent ie = new Intent(Intent.ACTION_VIEW, Uri.parse(restWeb));
				startActivity(ie);

				/*
				 * Intent intent = new Intent();
				 * 
				 * Bundle bundle = new Bundle(); bundle.putString("restWeb",
				 * restWeb); intent.putExtras(bundle);
				 * intent.setClass(PagerActivity.this, WebViewActivity.class);
				 * startActivity(intent);
				 */
				// PagerActivity.this.finish();
			}
		});

		// set restaurant price
		// restPrice = "平日午餐$239\n平日晚餐及假日全天$259";
		// TextView restPriceTV = (TextView)
		// infoLayout.findViewById(R.id.priceTV);
		// restPriceTV.setText(restPrice);

		// set restaurant rating
		RatingBar smallRatingBar = (RatingBar) infoLayout
				.findViewById(R.id.ratingBar1);
		// smallRatingBar.setEnabled(false);

		TextView restDescripTV = (TextView) infoLayout
				.findViewById(R.id.descriptionTV);
		restDescripTV.setText(restDescription);

	}

	public String getWebPage(String adresse) {

		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet();

		InputStream inputStream = null;

		String response = null;

		try {

			URI uri = new URI(adresse);
			httpGet.setURI(uri);

			HttpResponse httpResponse = httpClient.execute(httpGet);
			int statutCode = httpResponse.getStatusLine().getStatusCode();
			int length = (int) httpResponse.getEntity().getContentLength();

			// Log.v(LOG_THREAD_ACTIVITY, "HTTP GET: " + adresse);
			// Log.v(LOG_THREAD_ACTIVITY, "HTTP StatutCode: " + statutCode);
			// Log.v(LOG_THREAD_ACTIVITY, "HTTP Lenght: " + length + " bytes");

			inputStream = httpResponse.getEntity().getContent();
			Reader reader = new InputStreamReader(inputStream, "UTF-8");

			int inChar;
			StringBuffer stringBuffer = new StringBuffer();

			while ((inChar = reader.read()) != -1) {
				stringBuffer.append((char) inChar);
			}

			response = stringBuffer.toString();

		} catch (ClientProtocolException e) {
			// Log.e(LOG_THREAD_ACTIVITY,
			// "HttpActivity.getPage() ClientProtocolException error", e);
		} catch (IOException e) {
			// / Log.e(LOG_THREAD_ACTIVITY,
			// "HttpActivity.getPage() IOException error", e);
		} catch (URISyntaxException e) {
			// Log.e(LOG_THREAD_ACTIVITY,
			// "HttpActivity.getPage() URISyntaxException error", e);
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();

			} catch (IOException e) {
				// Log.e(LOG_THREAD_ACTIVITY,
				// "HttpActivity.getPage() IOException error lors de la fermeture des flux",
				// e);
			}
		}

		return response;
	}

	private class HttpTask extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... urls) {
			// TODO Auto-generated method stub
			String response = getWebPage(urls[0]);
			return response;
		}

		@Override
		protected void onPostExecute(String response) {
			// Log.i(LOG_THREAD_ACTIVITY, "HTTP RESPONSE" + response);

			TextView tmpTv = (TextView) layout3.findViewById(R.id.textViewP3);
			tmpTv.setText(response);
			
			Log.d("emo",response);
			//response = "No restaurant available";
			
			if (!response.equals("No restaurant available")) {

				String[] splitInfo = response.split("\t");

				if(splitInfo.length == 11)
				{
				
					restName = splitInfo[0]; // restaurant name
					imageFileURL = splitInfo[1]; // restaurant image url
					restAddr = splitInfo[2]; // restaurant address
					restTel = splitInfo[3]; // restaurant telephone number
					restOpen = splitInfo[4]; // restaurant opening time
					restClosed = splitInfo[5]; // restaurant closed days
					restWeb = splitInfo[6]; // restaurant web site
					restDescription = splitInfo[7]; // restaurant price
					restMenu = splitInfo[8];
					resLat = Double.parseDouble(splitInfo[9]);
					resLng = Double.parseDouble(splitInfo[10]);
				}

			} else {
				new AlertDialog.Builder(PagerActivity.this)
						.setTitle(response)
						.setPositiveButton("確定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// continue with delete
									}
								}).show();

			}

			/**
			 * set restaurant information in activity_restaurant
			 * **/
			setRestaurantInfo();

			/**
			 * set map
			 * **/
			setMap();

			/**
			 * set menu
			 * **/
			setMenu();
		}

		private void setMenu() {
			restMenu = "咖哩炒泡麵:80;麻辣炒泡麵:70;泰式炒泡麵:65;咖哩蛋炒飯:80;麻辣炒飯:90;";
			//restMenu = "nomenu";
			LinearLayout ll = (LinearLayout) findViewById(R.id.menuLayout);
			if (restMenu != null && !restMenu.equals("nomenu")) {
				splitMenu = restMenu.split(";");
				// TextView tv = (TextView) findViewById(R.id.);
				// ll.addview

				// restMenu.length()
				for (int i = 0; i < splitMenu.length; i++) {
					String[] splitFood = splitMenu[i].split(":");
					String food = splitFood[0];
					String price = splitFood[1];
					// View view = new View(null);
					// view.setc
					View view = View.inflate(PagerActivity.this,
							R.layout.menu_item_layout, null);
					TextView menuItemName = (TextView) view
							.findViewById(R.id.menuItemName);
					menuItemName.setText(food);
					menuItemName.setOnTouchListener(btTouchListener);
					menuItemName.setOnClickListener(fooditemclick);
					TextView menuItemValue = (TextView) view
							.findViewById(R.id.menuItemPrice);
					menuItemValue.setText("$" + price);
					TextView menuItemPlus = (TextView) view
							.findViewById(R.id.menuItemPlus);
					menuItemPlus.setOnClickListener(manuPlusMinusListener);
					menuItemPlus.setOnTouchListener(menuItemTouchListener);
					TextView menuItemMinus = (TextView) view
							.findViewById(R.id.menuItemMinus);
					menuItemMinus.setOnClickListener(manuPlusMinusListener);
					menuItemMinus.setOnTouchListener(menuItemTouchListener);

					ll.addView(view);

					// View.inflate(this, R.layout.menu_item_layout, null);
				}
			} else {

				TextView noMenu = new TextView(PagerActivity.this);
				noMenu.setText(restMenu);
				ll.addView(noMenu);
			}

			TextView comfirmTV = (TextView) menuLayout
					.findViewById(R.id.confirmTV);
			comfirmTV.setOnTouchListener(btTouchListener);
			comfirmTV.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					
					LinearLayout ll = (LinearLayout) findViewById(R.id.menuLayout);
					int viewCount = ll.getChildCount();
					
					if(confirmFlag)
					{
						confirmFlag = false;
						TextView confirmText = (TextView)v;
						confirmText.setText("Cancel!");
						
						for(int i = 0;i<viewCount;i++)
						{
							View itemView = ll.getChildAt(i);
							TextView itemCount = (TextView) itemView.findViewById(R.id.menuItemNumber);
							if(itemCount!=null)
							{
								if(itemCount.getText().toString().equals("0"))
								{
	
									itemView.findViewById(R.id.menuItemName).setEnabled(false);
									itemView.findViewById(R.id.menuItemPrice).setEnabled(false);
									itemView.findViewById(R.id.menuItemNumber).setEnabled(false);
									
									
								}
	
								itemView.findViewById(R.id.menuItemPlus).setEnabled(false);
								((TextView) itemView.findViewById(R.id.menuItemPlus)).setTextColor(Color.GRAY);
								itemView.findViewById(R.id.menuItemMinus).setEnabled(false);
								((TextView) itemView.findViewById(R.id.menuItemMinus)).setTextColor(Color.GRAY);
							}
						}
						/*
						SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmm");
						String datetime = s.format(new Date());
						for (String temp : mealItem) {
							ContentValues values = new ContentValues();
							values.put("restaurant", restName);
							values.put("menu", temp);
							values.put("date", datetime);
							values.put("rating", 3);
							values.put("comment", "");
							values.put("photo", "");
							Log.d("emo","inserting");
							long c = db.insertOrThrow("history", null, values);
							Log.d("emo","insert end, "+c);
							}
						*/
					}
					else
					{
						confirmFlag = true;
						TextView confirmText = (TextView)v;
						confirmText.setText("Confirm!");
						
						for(int i = 0;i<viewCount;i++)
						{
							View itemView = ll.getChildAt(i);
							TextView itemCount = (TextView) itemView.findViewById(R.id.menuItemNumber);
							if(itemCount!=null)
							{
								if(itemCount.getText().toString().equals("0"))
								{
	
									itemView.findViewById(R.id.menuItemName).setEnabled(true);
									itemView.findViewById(R.id.menuItemPrice).setEnabled(true);
									itemView.findViewById(R.id.menuItemNumber).setEnabled(true);
								}
	
								itemView.findViewById(R.id.menuItemPlus).setEnabled(true);
								((TextView) itemView.findViewById(R.id.menuItemPlus)).setTextColor(Color.BLACK);
								itemView.findViewById(R.id.menuItemMinus).setEnabled(true);
								((TextView) itemView.findViewById(R.id.menuItemMinus)).setTextColor(Color.BLACK);
								
							}
						}
					}
					
				}
			});
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

	}

	private class MyPagerAdapter extends PagerAdapter {

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			Log.d("k", "destroyItem");
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
			Log.d("k", "finishUpdate");
		}

		@Override
		public int getCount() {
			Log.d("k", "getCount");
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			Log.d("k", "instantiateItem");
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			return mListViews.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			Log.d("k", "isViewFromObject");
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			Log.d("k", "restoreState");
		}

		@Override
		public Parcelable saveState() {
			Log.d("k", "saveState");
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
			Log.d("k", "startUpdate");
		}

	}

	public void onBackPressed() {
		Intent intent = new Intent();
		intent.setClass(PagerActivity.this, MainActivity.class);
		startActivity(intent); 
		PagerActivity.this.finish(); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_pager, menu);
		return true;
	}

	private OnClickListener pagerTVListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			int id = v.getId();

			switch (id) {

			case R.id.infoTV:
				myViewPager.setCurrentItem(0);
				break;
			case R.id.menuTV:
				myViewPager.setCurrentItem(1);
				break;
			case R.id.mapTV:
				myViewPager.setCurrentItem(2);
				break;
			case R.id.commentTV:
				myViewPager.setCurrentItem(3);
				break;
			case R.id.myCommentTV:
				myViewPager.setCurrentItem(4);
				break;
			default:
				break;
			}
		}
	};

	public OnTouchListener btTouchListener = new OnTouchListener() {

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

	private OnTouchListener menuItemTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			TextView tv = (TextView) v;
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				v.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.border_corner));

				tv.setTextColor(Color.WHITE);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				v.setBackgroundDrawable(getResources().getDrawable(R.drawable.border));
				tv.setTextColor(Color.BLACK);
				// v.setBackgroundDrawable();
			}
			// TODO Auto-generated method stub
			return false;
		}
	};

	private OnClickListener manuPlusMinusListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			RelativeLayout parent = (RelativeLayout) v.getParent();
			TextView numberTV = (TextView) parent
					.findViewById(R.id.menuItemNumber);
			int number = 0;

			String numberString = numberTV.getText().toString();
			if (numberString != null && !numberString.equals("")) {
				number = Integer.parseInt(numberString);
			}

			TextView itemTV = (TextView) parent.findViewById(R.id.menuItemName);
			String itemName = (String) itemTV.getText();

			TextView totalPriceTV = (TextView) menuLayout
					.findViewById(R.id.totalTV);
			String totalPriceString = totalPriceTV.getText().toString();
			totalPriceString = totalPriceString.substring(1,
					totalPriceString.length());
			int totalPrice = Integer.parseInt(totalPriceString);

			TextView menuItemPriceTV = (TextView) parent
					.findViewById(R.id.menuItemPrice);
			String menuItemPriceString = menuItemPriceTV.getText().toString();
			menuItemPriceString = menuItemPriceString.substring(1,
					menuItemPriceString.length());
			int menuItemPrice = Integer.parseInt(menuItemPriceString);

			TextView tv = (TextView) v;

			if (tv.getText().equals("+")) {
				if (number == 0) {
					mealItem.add(itemName);
				}
				number++;
				totalPrice += menuItemPrice;
			} else {
				if (number > 0) {
					number--;
					totalPrice -= menuItemPrice;
				}
				if (number == 0) {
					mealItem.remove(itemName);
				}
			}
			numberTV.setText("" + number);
			totalPriceTV.setText("$" + totalPrice);
		}
	};

	private OnClickListener fooditemclick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			TextView tv = (TextView) v;
			String food = (String) tv.getText();
			Intent intent = new Intent();
			intent.setClass(PagerActivity.this, ItemActivity.class);

			Bundle bundle = new Bundle();
			bundle.putString("food", food);
			bundle.putString("restaurant", restName);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	};

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}
}
