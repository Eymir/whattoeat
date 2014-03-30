package com.example.whattoeat;


import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.view.Menu;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.MediaStore;


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
	//private View layout3 = null;
	private View commentLayout = null;
	private View myCommentLayout = null;
	
	
	// server
	//private String baseUrl ="http://myweb.ncku.edu.tw/~p96024061/testAndroid/index.php?";
	//private String baseUrl = "http://192.168.1.116/wteSuggest.php?";
	private String baseUrl = "http://140.116.86.212/api/";
	
	
	
	// restaurant info
	private int restId = 0;
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
	private String[] splitRes = null;
	private String restComments = null;
	
	
	//my commet
	private String myComment = null;
	private float myRate = 0;
	
	
	// flags
	//private String lastRes = null;
	//private String flag = "0";
	private boolean confirmFlag = true; 
	private int resRank = 0;
	private int delayDays = 0;
	private int priceBelow = 100;
	
	private final int numberOfResPerTime = 5;

	// google map settings
	private GoogleMap map;

	// user info
	private String deviceId;
	private Double longitude;
	private Double latitude;

	
	private ArrayList<String> mealItem;
	DBHelper helper = new DBHelper(PagerActivity.this);
	SQLiteDatabase db;

	private String  SDPATH = Environment.getExternalStorageDirectory().getPath() + "//";

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewpager_layout);
		/*
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			flag = bundle.getString("flag");

			if (bundle.size() > 1) {
				lastRes = bundle.getString("lastRes");
			}
		}

		db = helper.getWritableDatabase();*/
		mealItem = new ArrayList<String>();
				
		
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			delayDays = bundle.getInt("delayDays");

			if (bundle.size() > 1) {
				priceBelow = bundle.getInt("priceBelow");
			}
		}
		
		
		/** set pager layout **/
		setPagerItem();
		
		/** set buttons  **/
		setButton();
		
		
		/**
		 * send deviceId, GPS and last restaurant to server using http GET
		 * request and get restaurant information to initial
		 * **/

		initial();

		/**
		 * set restaurant information in activity_restaurant now at HttpTask
		 * **/
		// setRestaurantInfo();

		/**
		 * set map now at HttpTask
		 * **/
		// setMap();
		
		/**
		 * set setComment now at HttpTaskComment
		 * **/
		

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
		
		
		// device id
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		deviceId = telephonyManager.getDeviceId();
		//deviceId = "6642"; 

		// GPS
		LocationManager status = (LocationManager) (this
				.getSystemService(Context.LOCATION_SERVICE));
		if (status.isProviderEnabled(LocationManager.GPS_PROVIDER)
				|| status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			// 如果GPS或網路定位開啟，呼叫locationServiceInitial()更新位置
			locationServiceInitial();
		} else {	
			//alertWithSetting("請開啟定位服務",new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
		}

		latitude = 22.9;
		longitude = 120.0;

		File filePath = new File(SDPATH+"whattoeat");	//判斷目錄存不存在
		if(!filePath.exists()){
			filePath.mkdirs();
			//alert("!!!!");
		}
		
		
		
		String url = baseUrl+"query.php?" + "user_id=" + deviceId + "&latitude=" + latitude + "&longitude=" + longitude + "&index_start=" + (resRank) + "&index_end=" + (resRank+numberOfResPerTime)+"&delay_days="+delayDays+"&price="+priceBelow;
		new HttpTask().execute(url);
	}
	
	
	private void setButton()
	{
		// set restart
		TextView restartTV = (TextView) this.findViewById(R.id.restartTV);

		restartTV.setOnTouchListener(btTouchListener);
		restartTV.setOnClickListener(new OnClickListener() {

		@Override
		public void onClick(View v) 
		{
			resRank++;
			if( resRank % numberOfResPerTime != 0)
			{
				if(splitRes.length > (resRank%numberOfResPerTime))
				{
					setRestaurantInfo();
					setMap();
					setMenu();
					
					
					
					myViewPager.setCurrentItem(0);
					myViewPager.removeView(myCommentLayout);
					mListViews.remove(myCommentLayout);
					myAdapter.notifyDataSetChanged ();
					TextView tv = (TextView) findViewById(R.id.myCommentTV);
					tv.setVisibility(View.INVISIBLE);

				}
				else
				{
					alert("沒有選項了");
					resRank--;
				}
			}
			else
			{
				String url = baseUrl+"query.php?" + "user_id=" + deviceId + "&latitude=" + latitude + "&longitude=" + longitude + "&index_start=" + (resRank) + "&index_end=" + (resRank+numberOfResPerTime)+"&delay_days="+delayDays+"&price="+priceBelow;
				new HttpTask().execute(url);
				
				myViewPager.setCurrentItem(0);
				myViewPager.removeView(myCommentLayout);
				mListViews.remove(myCommentLayout);
				myAdapter.notifyDataSetChanged ();
				TextView tv = (TextView) findViewById(R.id.myCommentTV);
				tv.setVisibility(View.INVISIBLE);
			}
		}});

		// set last one
		TextView backTV = (TextView) this.findViewById(R.id.backTV);
		backTV.setOnTouchListener(btTouchListener);

		backTV.setOnClickListener(new OnClickListener() {

		@Override
		public void onClick(View v) 
		{
			if( resRank % numberOfResPerTime != 0)
			{
				resRank--;
				setRestaurantInfo();
				setMap();
				setMenu();
				
				myViewPager.setCurrentItem(0);
				myViewPager.removeView(myCommentLayout);
				mListViews.remove(myCommentLayout);
				myAdapter.notifyDataSetChanged ();
				TextView tv = (TextView) findViewById(R.id.myCommentTV);
				tv.setVisibility(View.INVISIBLE);
			}
			else
			{
				if(resRank!=0)
				{
					String url = baseUrl+"query.php?" + "user_id=" + deviceId + "&latitude=" + latitude + "&longitude=" + longitude + "&index_start=" + (resRank-numberOfResPerTime) + "&index_end=" + (resRank)+"&delay_days="+delayDays+"&price="+priceBelow;
					new HttpTask().execute(url);
					resRank--;
					
					myViewPager.setCurrentItem(0);
					myViewPager.removeView(myCommentLayout);
					mListViews.remove(myCommentLayout);
					myAdapter.notifyDataSetChanged ();
					TextView tv = (TextView) findViewById(R.id.myCommentTV);
					tv.setVisibility(View.INVISIBLE);
				}
				else
				{
					alert("回到最初的選項了");
				}
			}
		}});

		TextView leaveTV = (TextView) this.findViewById(R.id.leaveTV);
		leaveTV.setOnTouchListener(btTouchListener);
		leaveTV.setOnClickListener(new OnClickListener() {
			
		@Override
		public void onClick(View v) 
		{
			
			TextView tv = (TextView) findViewById(R.id.myCommentTV);
			tv.setVisibility(View.VISIBLE);
			if(!mListViews.contains(myCommentLayout))
			{
				mListViews.add(myCommentLayout);
	
				//myAdapter = new MyPagerAdapter();
				//myViewPager = (ViewPager) findViewById(R.id.viewpagerLayout);
				myViewPager.setAdapter(myAdapter);
				myViewPager.setCurrentItem(4);
				setMyComment();
			}
		}});
		
		//set confirm
		TextView confirmTV = (TextView) menuLayout.findViewById(R.id.confirmTV);
		confirmTV.setOnTouchListener(btTouchListener);
		confirmTV.setOnClickListener(new OnClickListener() {

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
		}});

				
		//set new photo
		Button newphotobtn = (Button) myCommentLayout.findViewById(R.id.newPhotobtn);
		newphotobtn.setOnTouchListener(btTouchListener);
		newphotobtn.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			File tmpFile = new File(Environment.getExternalStorageDirectory(),"whattoeat/"+restId+".jpg");
			Uri outputFileUri = Uri.fromFile(tmpFile);
						
			intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri); 
			startActivityForResult(intent, 0); 
			setMyComment();
		}});
		
		
		// send comment
		Button sendComment = (Button) myCommentLayout.findViewById(R.id.backbtn);
		sendComment.setOnTouchListener(btTouchListener);
		sendComment.setOnClickListener(new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			

			RatingBar bar = (RatingBar) myCommentLayout.findViewById(R.id.ratingBar1);
			myRate = bar.getRating();
			
			EditText et = (EditText) myCommentLayout.findViewById(R.id.editText1);
			myComment = et.getText().toString();
			
			
			
			String url = baseUrl+"comment.php?shop_id="+restId+"&user_id="+deviceId+"&grade="+myRate+"&comment="+myComment+"&flag=1";
			new HttpTaskMyComment().execute(url);

		}});
		
		
				
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
		commentLayout = mInflater.inflate(R.layout.comment_layout, null);
		
		myCommentLayout = mInflater.inflate(R.layout.activity_item, null);
		//layout3 = mInflater.inflate(R.layout.layout3, null);

		mListViews.add(infoLayout);
		mListViews.add(menuLayout);
		mListViews.add(mapLayout);
		mListViews.add(commentLayout);
		//mListViews.add(layout3);
		

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


	}

	

	@SuppressLint("NewApi")
	private void setRestaurantInfo() {
		
		String[] splitInfo = null;
		//if(splitRes.length > (resRank%numberOfResPerTime))
		//{
			splitInfo = splitRes[resRank%numberOfResPerTime].split("\t");
			/*
			TextView tmpTv = (TextView) layout3.findViewById(R.id.textViewP3);
			tmpTv.setText( resRank+"    "+splitRes[resRank%numberOfResPerTime]);
			*/
			String []latlng = null;

			if(splitRes[resRank%numberOfResPerTime].equals(""))
			{
				
			}
			else
			{
				if(splitInfo.length > 0)
				{
					restId = Integer.parseInt(splitInfo[0]);
					
					String url = baseUrl+"query_comment?shop_id="+restId;
					new HttpTaskComment().execute(url);
				}
				if(splitInfo.length > 1)
				{
					restName = splitInfo[1];
				}
				if(splitInfo.length > 2)
				{
					imageFileURL = splitInfo[2]; // restaurant image url
				}
				if(splitInfo.length > 3)
				{
					restAddr = splitInfo[3]; // restaurant address
				}
				if(splitInfo.length > 4)
				{
					restTel = splitInfo[4]; // restaurant telephone number
				}
				if(splitInfo.length > 5)
				{
					restOpen = splitInfo[5]; // restaurant opening time
				}
				if(splitInfo.length > 6)
				{
					restClosed = splitInfo[6]; // restaurant closed days
				}
				if(splitInfo.length > 7)
				{
					restWeb = splitInfo[7]; // restaurant web site
				}
				if(splitInfo.length > 8)
				{
					restDescription = splitInfo[8]; // restaurant price
				}
				if(splitInfo.length > 9)
				{
					restMenu = splitInfo[9];
				}
				if(splitInfo.length > 10)
				{
					latlng = splitInfo[10].split(",");
					if(latlng.length ==2)
					{	
						resLat = Double.parseDouble(latlng[0]);
						resLng = Double.parseDouble(latlng[1]);
					}
				}
			
			}
			
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
			//smallRatingBar.setRating(5);
			// smallRatingBar.setEnabled(false);

			TextView restDescripTV = (TextView) infoLayout
					.findViewById(R.id.descriptionTV);
			restDescripTV.setText(restDescription);
		/*}
		else
		{
			alert("沒有選項了!!");
		}
		*/
		

	}

	private void setMenu() {
		restMenu = "咖哩炒泡麵:80;麻辣炒泡麵:70;泰式炒泡麵:65;咖哩蛋炒飯:80;麻辣炒飯:90;咖哩炒泡麵:80;麻辣炒泡麵:70;泰式炒泡麵:65;咖哩蛋炒飯:80;麻辣炒飯:90;咖哩炒泡麵:80;麻辣炒泡麵:70;泰式炒泡麵:65;咖哩蛋炒飯:80;麻辣炒飯:90;咖哩炒泡麵:80;麻辣炒泡麵:70;泰式炒泡麵:65;咖哩蛋炒飯:80;麻辣炒飯:90;咖哩炒泡麵:80;麻辣炒泡麵:70;泰式炒泡麵:65;咖哩蛋炒飯:80;麻辣炒飯:90;咖哩炒泡麵:80;麻辣炒泡麵:70;泰式炒泡麵:65;咖哩蛋炒飯:80;麻辣炒飯:90;咖哩炒泡麵:80;麻辣炒泡麵:70;泰式炒泡麵:65;咖哩蛋炒飯:80;麻辣炒飯:90;";
		//restMenu = "nomenu";
		LinearLayout ll = (LinearLayout) menuLayout.findViewById(R.id.menuLayout);
		ll.removeAllViews();
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

		
	}
	
	private void setComment() {
		//restMenu = "nomenu";
		//restComments = "fish:5:asdasdasdasd;fish2:4:aaa;fish:1:ㄎㄎd;fish:5:asdasdasdasd;fish2:4:aaa;fish:1:ㄎㄎd;fish:5:asdasdasdasd;fish2:4:aaa;fish:1:ㄎㄎd;fish:5:asdasdasdasd;fish2:4:aaa;fish:1:ㄎㄎd;fish:5:asdasdasdasd;fish2:4:aaa;fish:1:ㄎㄎd;";

		LinearLayout ll2= (LinearLayout)commentLayout.findViewById(R.id.commentLayout);
		ll2.removeAllViews();
		
		/*
		TextView tmpTv = (TextView) layout3.findViewById(R.id.textViewP3);
		tmpTv.setText( restComments);
		*/
		
		String[] splitComments = null;
		//restComments = "123\t456\t789";
		splitComments = restComments.split("\t");
		
		
		//tmpTv.append( "                      "+splitComments.length+" "+splitComments[0]);
		
		myComment = "";
		myRate = 0;
		setMyComment();
		
		for (int i = 0; i < splitComments.length-(splitComments.length%3); i=i+3) 
		{
			String id = splitComments[i];
			float star = Float.parseFloat(splitComments[i+1]);
			String comment = splitComments[i+2];

			if(id.equals(deviceId))
			{
				myComment = comment;
				myRate = star;
				setMyComment();
			}
			else
			{

				View view = View.inflate(PagerActivity.this,R.layout.comment_item_layout, null);
				
				TextView commentId = (TextView) view
						.findViewById(R.id.commentIdTV);
				commentId.setText(id);
				
				RatingBar rb = (RatingBar) view.findViewById(R.id.commentRatingBar);
				rb.setRating(star);
				rb.setEnabled(false);
				
				TextView commentCommenr = (TextView) view
						.findViewById(R.id.commnentCommentTV);
				commentCommenr.setText(comment);

				ll2.addView(view);
			}
			

		}
		
	}
	
	
	private void setMyComment()
	{
		
		//alert(myComment+" "+myRate);
		TextView tv = (TextView) myCommentLayout.findViewById(R.id.itemTitle);
		tv.setText(restName);
		
		RatingBar bar = (RatingBar) myCommentLayout.findViewById(R.id.ratingBar1);
		bar.setRating(myRate);
		
		EditText et = (EditText) myCommentLayout.findViewById(R.id.editText1);
		et.setText(myComment);
		
		ImageView iv = (ImageView) myCommentLayout.findViewById(R.id.view1);
		
		File imgFile = new File(SDPATH+"whattoeat/"+restId+".jpg");
		if(imgFile.exists()) {
			
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = false;
			options.inPreferredConfig = Config.RGB_565;
			options.inSampleSize = 2;
		    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(),options);
		    iv.setImageBitmap(myBitmap);
		}
		else
		{
			iv.setImageBitmap(null);
		}
		
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
/*
			TextView tmpTv = (TextView) layout3.findViewById(R.id.textViewP3);
			tmpTv.setText(response);
			*/
			Log.d("emo",response);
			//response = "No restaurant available";
			
			if (!response.equals("No restaurant available")) {

				//response = " \n \n \n ";
				splitRes = response.split("\n");
				
				
/*
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
				}*/

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

	private class HttpTaskComment extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... urls) {
			// TODO Auto-generated method stub
			String response = getWebPage(urls[0]);
			return response;
		}

		@Override
		protected void onPostExecute(String response) {
				
			restComments = response;
			
			/*TextView tmpTv = (TextView) layout3.findViewById(R.id.textViewP3);
			tmpTv.setText( restComments);*/
			
			
			setComment();
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
	
	private class HttpTaskMyComment extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... urls) {
			// TODO Auto-generated method stub
			String response = getWebPage(urls[0]);
			return response;
		}

		@Override
		protected void onPostExecute(String response) {
				
			
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
			else if(event.getAction() == MotionEvent.ACTION_CANCEL)
			{
				v.setBackgroundDrawable(getResources().getDrawable(R.drawable.border));
				tv.setTextColor(Color.BLACK);
			}
			// TODO Auto-generated method stub
			return false;
		}

	};//

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
			else if(event.getAction() == MotionEvent.ACTION_CANCEL)
			{
				v.setBackgroundDrawable(getResources().getDrawable(R.drawable.border));
				tv.setTextColor(Color.BLACK);
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

	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK ) {
			
			ImageView iv = (ImageView) myCommentLayout.findViewById(R.id.view1);
			
			File imgFile = new File(SDPATH+"whattoeat/"+restId+".jpg");
			if(imgFile.exists()) {
				
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = false;
				options.inPreferredConfig = Config.RGB_565;
				options.inSampleSize = 2;
			    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(),options);
			    iv.setImageBitmap(myBitmap);
			}
			
		} else if (resultCode == Activity.RESULT_CANCELED ) {
			
		}
	}
	
	
	
	
	
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
	private void alert(String message)
	{
		new AlertDialog.Builder(PagerActivity.this)
		.setTitle(message)
		.setPositiveButton("確定",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						
						// continue with delete
					}
				}).show();
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

