package com.example.whattoeat;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.Menu;











import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;






import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
	private String imageFileURL = "http://pic.pimg.tw/bunnylinn/4bf0b91869bd0.jpg";
	
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
		tv.setBackgroundColor(Color.parseColor("#ff9801"));
		
       // EditText v2EditText = (EditText)layout2.findViewById(R.id.editText1);
       // v2EditText.setText("???�m�ĤG?view����");
        
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
					tv.setBackgroundColor(Color.parseColor("#ff9801"));
				}
				if(arg0 == 1)
				{
					tv = (TextView)findViewById(R.id.menuTV);
					tv.setBackgroundColor(Color.parseColor("#ff9801"));
				}
				if(arg0 == 2)
				{
					tv = (TextView)findViewById(R.id.mapTV);
					tv.setBackgroundColor(Color.parseColor("#ff9801"));
				}
				if(arg0 == 3)
				{
					tv = (TextView)findViewById(R.id.commentTV);
					tv.setBackgroundColor(Color.parseColor("#ff9801"));
				}
				/*
				View v = mListViews.get(arg0);
				EditText editText = (EditText)v.findViewById(R.id.editText1);
				editText.setText("???�m#"+arg0+"edittext���󪺭�");*/
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
		 * set restaurant image in activity_restaurant
		 * **/
        
        ImageView myImageView = (ImageView) infoLayout.findViewById(R.id.restaurantImgView);
        // myImageView.setImageResource(R.drawable.test1);
	    UrlImageViewHelper.setUrlDrawable(myImageView, imageFileURL);
		
        
        
       


        
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
