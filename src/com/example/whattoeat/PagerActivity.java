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
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;


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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


@SuppressLint("NewApi")
public class PagerActivity extends Activity implements LocationListener 
{
	//pager and layout
	private ViewPager myViewPager;
	private MyPagerAdapter myAdapter;
	private LayoutInflater mInflater;
	private List<View> mListViews;
	private View infoLayout = null;
	private View layout1 = null;
	private View mapLayout = null;
	private View layout3 = null;
	
	
	//server
	//private String baseUrl = "http://myweb.ncku.edu.tw/~p96024061/testAndroid/index.php?";
	private String baseUrl = "http://192.168.1.101/wteSuggest.php?";
	
	//restaurant info 
	private String imageFileURL = null;		// restaurant image url
	private String restName = null;	// restaurant name
	private String restAddr = null;	// restaurant address
	private String restTel = null;			// restaurant telephone number
	private String restOpen = null;			// restaurant opening time
	private String restClosed = null;		// restaurant closed days
	private String restParking = null;		// restaurant parking places
	private String restWeb = null;	// restaurant web site 
	private String restPrice = null;		// restaurant price
	private int rating;
    private Double resLat;
	private Double resLng;
	
	//flags
	private String lastRes = null;
	private String flag = "0";
	

	//google map settings
    private GoogleMap map;
    private Marker myMarker;
    
    //user info
    private String deviceId;
    private Double longitude;
    private Double latitude;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewpager_layout);
		

		
	    Bundle bundle = getIntent().getExtras();
	    if(bundle != null)
	    {
	    	flag = bundle.getString("flag");
	    	
	    	if(bundle.size()>1)
	    	{
	    		lastRes = bundle.getString("lastRes");
	    	}
	    }
		
		
		
		
		/**set pager layout**/
		setPagerItem();
      
		
		/**
	     * send deviceId, GPS and last restaurant to server using http GET request 
	     * and get restaurant information to initial
	     * **/
		
		TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	    deviceId = telephonyManager.getDeviceId();
	    
	    initial();
		
	   
	   /**
  	 	* set restaurant information in activity_restaurant
  	 	* now at httpSynk
  	 	* **/
  		//setRestaurantInfo();
			
  		/**
  		 * set map
  		 * now at httpSynk
  		 * **/
  		//setMap();
	   

        
    //    List<Address> addresses;
      //  Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
       

	}
	
	private void locationServiceInitial() {
		LocationManager lms = (LocationManager) getSystemService(LOCATION_SERVICE);	//取得系統定位服務
		Criteria criteria = new Criteria();	//資訊提供者選取標準
		String bestProvider = LocationManager.GPS_PROVIDER;
		bestProvider = lms.getBestProvider(criteria, true);	//選擇精準度最高的提供者
		Location location = lms.getLastKnownLocation(bestProvider);
		//Location location = lms.getLastKnownLocation(LocationManager.GPS_PROVIDER);	//使用GPS定位座標
		if(location != null)
		{
			longitude = location.getLongitude();	//取得經度
			latitude = location.getLatitude();	//取得緯度
			
		}
		else {
			Toast.makeText(this, "無法定位座標", Toast.LENGTH_LONG).show();
		}
	}
	private void initial()
	{
		
		// GPS
		  LocationManager status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
			if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				//如果GPS或網路定位開啟，呼叫locationServiceInitial()更新位置
				locationServiceInitial();
			} else {
				Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();
				startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));	//開啟設定頁面
			}
		
			if(lastRes!=null)
			{
				try {
					lastRes = URLEncoder.encode(lastRes, "utf-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			latitude = 22.9;
			longitude = 120.0;
			String url = baseUrl+"deviceId="+deviceId+"&lat="+latitude+"&lng="+longitude+"&lastRes="+lastRes+"&flag="+flag;
			//HttpTask a = new HttpTask();
			//a.execute(url);
			new HttpTask().execute(url);
			
			
			
	
	}
	
	
	
	
	private void setMap()
	{
		//set map
		
		
		 map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		//LatLng latlng = new LatLng(resLat,resLng);
		 //Marker marker = map.addMarker(new MarkerOptions().position(latlng).title(restName).snippet(restAddr));
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
        protected void onPostExecute(List<HashMap<String,String>> list)
        {
 
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
	
        
	    
	    //set restart
	    TextView restartTv = (TextView)this.findViewById(R.id.restartTV);
	    
	    restartTv.setOnClickListener(new OnClickListener() {
	    	
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				//restName = "XD 貓貓鍋";
				bundle.putString("flag", "1");
				intent.putExtras(bundle);
				
				intent.setClass(PagerActivity.this, PagerActivity.class);
				startActivity(intent); 
				

			}
		});
        
        
	    //set last one
	    TextView backTv = (TextView)this.findViewById(R.id.backTV);
	    
	    backTv.setOnClickListener(new OnClickListener() {
	    	
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				
				//restName = "貓貓鍋";
				bundle.putString("lastRes", restName);
				bundle.putString("flag", "2");
				intent.putExtras(bundle);
				intent.setClass(PagerActivity.this, PagerActivity.class);
				startActivity(intent); 
				

			}
		});
        
        
        
	}
	
	
	@SuppressLint("NewApi")
	private void setRestaurantInfo()
	{

		//set restaurant image 
		//imageFileURL = "http://pic.pimg.tw/bunnylinn/4bf0b91869bd0.jpg";
        ImageView restIV = (ImageView) infoLayout.findViewById(R.id.restaurantImgView);
	    UrlImageViewHelper.setUrlDrawable(restIV, imageFileURL);
	      
	    //set restaurant name
	   // restName = "XM麻辣鍋";
	    TextView restNameTV = (TextView) infoLayout.findViewById(R.id.titleTV);
	    restNameTV.setText(restName);
	    
	    //set restaurant address
	    //restAddr = "台南市東區林森路一段167號";
	    TextView restAddrTV = (TextView) infoLayout.findViewById(R.id.addressTV);
	    restAddrTV.setText(restAddr);
		
	    //set restaurant phone #
	    //restTel = "06-2083775";
	    TextView restPhoneTV = (TextView) infoLayout.findViewById(R.id.phoneTV);
	    restPhoneTV.setText(restTel);
		
	    //set restaurant opening time
	    //restOpen = "11:00-02:00";
	    TextView restOpenTV = (TextView) infoLayout.findViewById(R.id.openTimeTV);
	    restOpenTV.setText(restOpen);
	    
	    //set restaurant closed days
	    //restClosed = "無";
	    TextView restCloseTV = (TextView) infoLayout.findViewById(R.id.closedDaysTV);
	    restCloseTV.setText(restClosed);
	    
	    //set restaurant parking places
	    //restParking = "特約停車場";
	    TextView restParkingTV = (TextView) infoLayout.findViewById(R.id.parkingTV);
	    restParkingTV.setText(restParking);
	    
	    //set restaurant web site
	    //restWeb = "http://www.xm.512g.com";
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
	    //restPrice = "平日午餐$239\n平日晚餐及假日全天$259";
	    TextView restPriceTV = (TextView) infoLayout.findViewById(R.id.priceTV);
	    restPriceTV.setText(restPrice);
    
	    //set restaurant rating
	   RatingBar smallRatingBar = (RatingBar) infoLayout.findViewById(R.id.ratingBar1);
	   //smallRatingBar.setEnabled(false);
	   


	    
	    
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
	       // Log.e(LOG_THREAD_ACTIVITY, "HttpActivity.getPage() ClientProtocolException error", e);
	    } catch (IOException e) {
	      ///  Log.e(LOG_THREAD_ACTIVITY, "HttpActivity.getPage() IOException error", e);
	    } catch (URISyntaxException e) {
	       // Log.e(LOG_THREAD_ACTIVITY, "HttpActivity.getPage() URISyntaxException error", e);
	    } finally {
	        try {
	            if (inputStream != null)
	                inputStream.close();

	        } catch (IOException e) {
	         //   Log.e(LOG_THREAD_ACTIVITY, "HttpActivity.getPage() IOException error lors de la fermeture des flux", e);
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
	//        Log.i(LOG_THREAD_ACTIVITY, "HTTP RESPONSE" + response);
	        
	    	TextView tmpTv = (TextView)layout3.findViewById(R.id.textViewP3);
	    	tmpTv.setText(response);
	    	/*
	    	String [] splitInfo = response.split("\t");
	    	
	    	restName = splitInfo[0];	// restaurant name
	    	imageFileURL = splitInfo[1];		// restaurant image url
	    	restAddr = splitInfo[2];	// restaurant address
	    	restTel = splitInfo[3];			// restaurant telephone number
	    	restOpen = splitInfo[4];			// restaurant opening time
	    	restClosed = splitInfo[5];		// restaurant closed days
	    	restParking = splitInfo[6];		// restaurant parking places
	    	restWeb = splitInfo[7];	// restaurant web site 
	    	restPrice = splitInfo[8];		// restaurant price
	    */

	    	/**
	   	 	* set restaurant information in activity_restaurant
	   	 	* **/
	   		setRestaurantInfo();
				
	   		/**
	   		 * set map
	   		 * **/
	   		setMap();
	    	 
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
