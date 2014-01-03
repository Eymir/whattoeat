package com.example.whattoeat;



import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.wallet.Address;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

@SuppressLint("NewApi")
public class PagerActivity extends Activity {
	private ViewPager myViewPager;

	private MyPagerAdapter myAdapter;
	
	private LayoutInflater mInflater;
	private List<View> mListViews;
	private View infoLayout = null;
	private View layout1 = null;
	private View mapLayout = null;
	private View layout3 = null;

	private String restWeb = null;
	private String imageFileURL = null;	
	private String restAddr = null;
	private String restName = null;
	
	//static final LatLng NKUT = new LatLng(23.979548, 120.696745);
    private GoogleMap map;
    private Marker myMarker;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewpager_layout);
		
		
		/**set pager**/
		setPagerItem();
      
        /**
         * set restaurant information in activity_restaurant
		 * **/
        setRestaurantInfo();
        
        
        /**
         * set map
         * **/
        setMap();
        
        

        
    //    List<Address> addresses;
      //  Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
       



        
	}
	
	
	
	private void setMap()
	{
		//set map
		
		 map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		// Marker nkut = map.addMarker(new MarkerOptions().position(NKUT).title("南開科技大學").snippet("數位生活創意系"));
	     // Move the camera instantly to NKUT with a zoom of 16.
	     //map.moveCamera(CameraUpdateFactory.newLatLngZoom(NKUT, 16));
		
		
	        
	        if(restAddr==null || restAddr.equals("")){
	            //Toast.makeText(getBaseContext(), "No Place is entered", Toast.LENGTH_SHORT).show();
	            return;
	        }

	        String url = "https://maps.googleapis.com/maps/api/geocode/json?";
	        String encodedAddress = null;
	        
	        try {
	            // encoding special characters like space in the user input place
	        	encodedAddress = URLEncoder.encode(restAddr, "utf-8");
	        } catch (UnsupportedEncodingException e) {
	            e.printStackTrace();
	        }

	        String address = "address=" + encodedAddress;

	        String sensor = "sensor=false";

	        // url , from where the geocoding data is fetched
	        url = url + address + "&" + sensor;

	        // Instantiating DownloadTask to get places from Google Geocoding service
	        // in a non-ui thread
	        DownloadTask downloadTask = new DownloadTask();

	        // Start downloading the geocoding places
	        downloadTask.execute(url);
		
		
		
	}
	
	
	
	
	
	private String downloadUrl(String strUrl) throws IOException
	{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
 
            // Connecting to url
            urlConnection.connect();
 
            // Reading data from url
            iStream = urlConnection.getInputStream();
 
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
 
            StringBuffer sb = new StringBuffer();
 
            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }
 
            data = sb.toString();
            br.close();
 
        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
 
        return data;
    }
	
	
	/** A class, to download Places from Geocoding webservice */
    private class DownloadTask extends AsyncTask<String, Integer, String>{
 
        String data = null;
 
        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try{
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }
        @Override
        protected void onPostExecute(String result){
 
            // Instantiating ParserTask which parses the json data from Geocoding webservice
            // in a non-ui thread
            ParserTask parserTask = new ParserTask();
 
            // Start parsing the places in JSON format
            // Invokes the "doInBackground()" method of the class ParseTask
            parserTask.execute(result);
        }
    }
    
    class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{
    	 
        JSONObject jObject;
 
        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String,String>> doInBackground(String... jsonData) {
 
            List<HashMap<String, String>> places = null;
            GeocodeJSONParser parser = new GeocodeJSONParser();
 
            try{
                jObject = new JSONObject(jsonData[0]);
 
                /** Getting the parsed data as a an ArrayList */
                places = parser.parse(jObject);
 
            }catch(Exception e){
                Log.d("Exception",e.toString());
            }
            return places;
        }
 
        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String,String>> list){
 
            // Clears all the existing markers
        	map.clear();
 
            for(int i=0;i<list.size();i++){
 
                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();
 
                // Getting a place from the places list
                HashMap<String, String> hmPlace = list.get(i);
 
                // Getting latitude of the place
                double lat = Double.parseDouble(hmPlace.get("lat"));
 
                // Getting longitude of the place
                double lng = Double.parseDouble(hmPlace.get("lng"));
 
                // Getting name
                String name = hmPlace.get("formatted_address");
 
                LatLng latLng = new LatLng(lat, lng);
 
                // Setting the position for the marker
                markerOptions.position(latLng);
 
                // Setting the title for the marker
                markerOptions.title(restName);
                markerOptions.snippet(restAddr);
                // Placing a marker on the touched position
                map.addMarker(markerOptions);
 
                // Locate the first location
                if(i==0)
                	//map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                	myMarker = map.addMarker(new MarkerOptions().position(latLng).title(restName).snippet(restAddr));
                	myMarker.showInfoWindow();
                	map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            }
        }
    }
	
	private void setPagerItem()
	{
		
		
		//set pager layout
        mListViews = new ArrayList<View>();
        mInflater = getLayoutInflater();
        infoLayout = mInflater.inflate(R.layout.activity_restaurunt, null);
        layout1 = mInflater.inflate(R.layout.layout1, null);
        mapLayout = mInflater.inflate(R.layout.activity_map, null);
        layout3 = mInflater.inflate(R.layout.layout3, null);
       
        mListViews.add(infoLayout);
        mListViews.add(layout1);
        mListViews.add(mapLayout);
        mListViews.add(layout3);
        
        
        myAdapter = new MyPagerAdapter();
		myViewPager = (ViewPager) findViewById(R.id.viewpagerLayout);
		myViewPager.setAdapter(myAdapter);
		
		//set pager item
		myViewPager.setCurrentItem(0);
		TextView tv = (TextView)findViewById(R.id.infoTV);
		tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_corner));
		tv.setOnClickListener(pagerTVListener);
		tv = (TextView)findViewById(R.id.menuTV);
		tv.setOnClickListener(pagerTVListener);
		tv = (TextView)findViewById(R.id.mapTV);
		tv.setOnClickListener(pagerTVListener);
		tv = (TextView)findViewById(R.id.commentTV);
		tv.setOnClickListener(pagerTVListener);
		
		
        
		
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
	
	}
	
	
	@SuppressLint("NewApi")
	private void setRestaurantInfo()
	{
										// restaurant image url
										// restaurant name
										// restaurant address
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
				
				Intent ie = new Intent(Intent.ACTION_VIEW,Uri.parse(restWeb));
		        startActivity(ie);
				
				/*
				Intent intent = new Intent();
				
				Bundle bundle = new Bundle();
				bundle.putString("restWeb", restWeb);
				intent.putExtras(bundle);
				intent.setClass(PagerActivity.this, WebViewActivity.class);
				startActivity(intent);
				*/ 
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
				/*
				Intent intent = new Intent();
				intent.setClass(PagerActivity.this, MapActivity.class);
				startActivity(intent); 
				*/
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

	
	private OnClickListener pagerTVListener    =   new OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
        	
        	int id = v.getId();
        	
        	switch (id)
        	{
        	
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
        	default:
        		break;
        	}
        	
        	
        	
        }
    };
}
