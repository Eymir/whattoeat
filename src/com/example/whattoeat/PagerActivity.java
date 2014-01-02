package com.example.whattoeat;


import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.Menu;

import java.util.ArrayList;
import java.util.List;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class PagerActivity extends Activity {
	private ViewPager myViewPager;

	private MyPagerAdapter myAdapter;
	
	private LayoutInflater mInflater;
	private List<View> mListViews;
	private View infoLayout = null;
	private View layout1 = null;
	private View layout2 = null;
	private View layout3 = null;

	private String restWeb = null;
	private String imageFileURL = null;	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewpager_layout);
		
		 
		
		

		/**set pager**/
        mListViews = new ArrayList<View>();
        mInflater = getLayoutInflater();
        infoLayout = mInflater.inflate(R.layout.activity_restaurunt, null);
        layout1 = mInflater.inflate(R.layout.layout1, null);
        layout2 = mInflater.inflate(R.layout.layout2, null);
        layout3 = mInflater.inflate(R.layout.layout3, null);
       
        mListViews.add(infoLayout);
        mListViews.add(layout1);
        mListViews.add(layout2);
        mListViews.add(layout3);
        
        
        myAdapter = new MyPagerAdapter();
		myViewPager = (ViewPager) findViewById(R.id.viewpagerLayout);
		myViewPager.setAdapter(myAdapter);
		
		myViewPager.setCurrentItem(0);
		TextView tv = (TextView)findViewById(R.id.infoTV);
		tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_corner));
		
       // EditText v2EditText = (EditText)layout2.findViewById(R.id.editText1);
       // v2EditText.setText("???置第二?view的值");
        
        myViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				Log.d("k", "onPageSelected - " + arg0);
				
				TextView tv = (TextView)findViewById(R.id.infoTV);
				tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_only_botton));
				tv = (TextView)findViewById(R.id.menuTV);
				tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_only_botton));
				tv = (TextView)findViewById(R.id.mapTV);
				tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_only_botton));
				tv = (TextView)findViewById(R.id.commentTV);
				tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_only_botton));
				
				if(arg0 == 0)
				{
					tv = (TextView)findViewById(R.id.infoTV);
					tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_corner));
					//tv.setBackgroundColor(Color.parseColor("#ff9801"));
				}
				if(arg0 == 1)
				{
					tv = (TextView)findViewById(R.id.menuTV);
					tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_corner));
					//tv.setBackgroundColor(Color.parseColor("#ff9801"));
				}
				if(arg0 == 2)
				{
					tv = (TextView)findViewById(R.id.mapTV);
					tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_corner));
					//tv.setBackgroundColor(Color.parseColor("#ff9801"));
				}
				if(arg0 == 3)
				{
					tv = (TextView)findViewById(R.id.commentTV);
					tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_corner));
					//tv.setBackgroundColor(Color.parseColor("#ff9801"));
				}
				/*
				View v = mListViews.get(arg0);
				EditText editText = (EditText)v.findViewById(R.id.editText1);
				editText.setText("???置#"+arg0+"edittext控件的值");*/
			}
			
			
			
			
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				Log.d("k", "onPageScrolled - " + arg0);
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				Log.d("k", "onPageScrollStateChanged - " + arg0);
				
				/**
			     * Indicates that the pager is in an idle, settled state. The current page
			     * is fully in view and no animation is in progress.
			     */
			    //public static final int SCROLL_STATE_IDLE = 0;
			    /**
			     * Indicates that the pager is currently being dragged by the user.
			     */
			    //public static final int SCROLL_STATE_DRAGGING = 1;
			    /**
			     * Indicates that the pager is in the process of settling to a final position.
			     */
			    //public static final int SCROLL_STATE_SETTLING = 2;

			}
		});
		
       
		
      
        
        /**
         * set restaurant information in activity_restaurant
		 * **/
        setRestaurantInfo();
        
       


        
	}
	
	
	
	private void setRestaurantInfo()
	{
										// restaurant image url
		String restName = null;			// restaurant name
		String restAddr = null;			// restaurant address
		String restTel = null;			// restaurant telephone number
		String restOpen = null;			// restaurant opening time
		String restClosed = null;		// restaurant closed days
		String restParking = null;		// restaurant parking places
										// restaurant web site 
		String restPrice = null;		// restaurant price
		
		
		
		//set restaurant image 
		imageFileURL = "http://pic.pimg.tw/bunnylinn/4bf0b91869bd0.jpg";
        ImageView restIV = (ImageView) infoLayout.findViewById(R.id.restaurantImgView);
	    UrlImageViewHelper.setUrlDrawable(restIV, imageFileURL);
	    
	    //ImageView image=(ImageView)findViewById(R.id.small_image);
      
	    
	    /*
	    restIV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//Intent ie = new Intent(Intent.ACTION_VIEW,Uri.parse(imageFileURL));
		        //startActivity(ie);
				//
				
				Intent intent = new Intent();
				
				Bundle bundle = new Bundle();
				bundle.putString("restWeb", imageFileURL);
				intent.putExtras(bundle);
				intent.setClass(PagerActivity.this, WebViewActivity.class);
				startActivity(intent); 
				//PagerActivity.this.finish(); 
			}
		});
	    */
	    
	
	    
	    //set restaurant name
	    restName = "XM麻辣鍋";
	    TextView restNameTV = (TextView) infoLayout.findViewById(R.id.titleTV);
	    restNameTV.setText(restName);
	    
	    //set restaurant address
	    restAddr = "台南市東區林森路一段167號";
	    TextView restAddrTV = (TextView) infoLayout.findViewById(R.id.addressTV);
	    restAddrTV.setText(restAddr);
		
	    //set restaurant phone #
	    restTel = "06-2083775";
	    TextView restPhoneTV = (TextView) infoLayout.findViewById(R.id.phoneTV);
	    restPhoneTV.setText(restTel);
		
	    //set restaurant opening time
	    restOpen = "11:00-02:00";
	    TextView restOpenTV = (TextView) infoLayout.findViewById(R.id.openTimeTV);
	    restOpenTV.setText(restOpen);
	    
	    //set restaurant closed days
	    restClosed = "無";
	    TextView restCloseTV = (TextView) infoLayout.findViewById(R.id.closedDaysTV);
	    restCloseTV.setText(restClosed);
	    
	    //set restaurant parking places
	    restParking = "特約停車場";
	    TextView restParkingTV = (TextView) infoLayout.findViewById(R.id.parkingTV);
	    restParkingTV.setText(restParking);
	    
	    //set restaurant web site
	    restWeb = "http://www.xm.512g.com";
	    TextView restWebTV = (TextView) infoLayout.findViewById(R.id.webTV);
	    restWebTV.setText(restWeb);
	    restWebTV.setTextColor(Color.BLUE);
	    restWebTV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//Intent ie = new Intent(Intent.ACTION_VIEW,Uri.parse(restWeb));
		        // startActivity(ie);
				
				
				Intent intent = new Intent();
				
				Bundle bundle = new Bundle();
				bundle.putString("restWeb", restWeb);
				intent.putExtras(bundle);
				intent.setClass(PagerActivity.this, WebViewActivity.class);
				startActivity(intent); 
				//PagerActivity.this.finish(); 
			}
		});
	    
	    
	    
	    //set restaurant price
	    restPrice = "平日午餐$239\n平日晚餐及假日全天$259";
	    TextView restPriceTV = (TextView) infoLayout.findViewById(R.id.priceTV);
	    restPriceTV.setText(restPrice);
	    
	    
	    //set restart
	    TextView restartTv = (TextView)this.findViewById(R.id.backTV);
	    
	    restartTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
	
				
			}
		});
	    
	}
	
	
	private class MyPagerAdapter extends PagerAdapter{

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
			((ViewPager) arg0).addView(mListViews.get(arg1),0);
			return mListViews.get(arg1);
		}
		
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			Log.d("k", "isViewFromObject");
			return arg0==(arg1);
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
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_pager, menu);
		return true;
	}

	
}
